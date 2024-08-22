@file:OptIn(ExperimentalStdlibApi::class)

package dev.toastbits.lifelog.application.git

import okio.FileSystem
import okio.Inflater
import okio.Path
import okio.Path.Companion.toPath
import java.nio.ByteBuffer
import java.security.MessageDigest
import kotlin.experimental.and

var currentObjectIndex: Int = -1

class GitCloner {
    private lateinit var objectTree: MutableMap<String, GitObject>

    private val sha1: MessageDigest by lazy { MessageDigest.getInstance("SHA-1") }
    private val inflatedBytes: ByteArray = ByteArray(1048576)
    private lateinit var reader: ByteReader

    private fun writeObject(obj: GitObject) {
        objectTree[obj.hash] = obj
    }

    private fun readObject(ref: String): GitObject = objectTree[ref]!!

    private fun preparePackFileBytes(bytes: ByteArray): ByteArray {
        val packLines: MutableList<IntRange> = mutableListOf()
        var currentRegion = bytes.indices

        while (currentRegion.size > 0) {
            val lineLen: Int = bytes.decodeToString(currentRegion.first, currentRegion.first + 4).toInt(16)
            if (lineLen == 0) {
                break
            }
            packLines.add(currentRegion.first + 4 until currentRegion.first + lineLen)
            currentRegion = currentRegion.first + lineLen .. currentRegion.last
        }

        return packLines.drop(1).map { (it.first + 1) .. it.last }.map { bytes.sliceArray(it) }.reduce { a, b -> a + b }
    }

    fun parse(mainRef: String, bytes: ByteArray) {
        objectTree = mutableMapOf()

        val packFile: ByteArray = preparePackFileBytes(bytes)
        val head: Int = packFile.indexOfOrNull("PACK".encodeToByteArray())!! + 4
        reader = ByteReader(packFile, head, inflatedBytes)

        val bytesHeader: PackFileHeader = reader.parsePackFileHeader()
        check(bytesHeader.version == 2) { bytesHeader }
        check(bytesHeader.objectCount > 0) { bytesHeader }

        println("Parsing ${bytesHeader.objectCount} objects...")

        for (objectIndex in 0 until bytesHeader.objectCount) {
            currentObjectIndex = objectIndex
            parseAnyObject()
        }

        println("Rendering tree...")

        renderCommit(mainRef)
    }

    private fun ByteArray.getSha1(byteCount: Int = size): String {
        sha1.update(this, 0, byteCount)
        return sha1.digest().toHexString()
    }

    private fun createGitObject(type: GitObject.Type, content: ByteArray, contentSize: Int): GitObject {
        // TODO | Optimise?
        val prefixBytes: ByteArray = "${type.identifier} $contentSize".encodeToByteArray() + 0b0
        val finalBytes: ByteArray = prefixBytes + content.take(contentSize)

        val hash: String = finalBytes.getSha1(finalBytes.size)
        return GitObject(finalBytes, type, hash)
    }

    private fun ByteReader.parseRawContentObject(type: GitObject.Type, expectedContentSize: Int) {
        val actualSize: Int = parseContent(expectedContentSize)
        val gitObject: GitObject = createGitObject(type, inflatedBytes, actualSize)
        writeObject(gitObject)
    }

    private fun parseAnyObject() {
        val (contentSize: Int, objectType: GitObject.Type) = reader.parseSizeAndTypeHeader()

        when (objectType) {
            GitObject.Type.COMMIT,
            GitObject.Type.TREE,
            GitObject.Type.TAG,
            GitObject.Type.BLOB -> {
                println("$currentObjectIndex | $objectType | $contentSize")
                reader.parseRawContentObject(objectType, contentSize)
            }
            GitObject.Type.REF_DELTA -> {
                val objRef = reader.bytes.toHexString(reader.head, reader.head + 20)
                reader.head += 20

                val obj = readObject(objRef)
                val contentSize: Int = reader.parseContent(null)
                val content = inflatedBytes

                val contentReader = ByteReader(content, 0, byteArrayOf())

                contentReader.parseSizeHeader()
                contentReader.parseSizeHeader()

//                println("FIRST BYTES $objRef")
//                println(content.toHexString(contentReader.head, contentReader.head + 20))
//                println(contentReader.head)
//                println(contentSize - contentReader.head)

                var targetContent: ByteArray? = null

//                println("---TEST START---")

//                val byte: Int = "9f".hexToByte().toInt()
//                println(0 or (byte shl 0))
//                val byte = "05"

//                val byte: Byte = "b0".hexToByte()
//                var dataPtr: Int = contentReader.head + 1
//                var size = 0L
//                for (i in 0 until 3) {
//                    val v: Long = byte.toLong() and (1L shl (i + 4))
//                    if (v != 0L) {
//                        size = size or (content[dataPtr++].toLong() shl (i * 8))
//                        println("SIZE TO $size ${content[dataPtr - 1].toHexString()}")
//                    }
//                }
//                TODO()

                while (contentReader.head < contentSize) {
                    val isCopy: Boolean = (content[contentReader.head].toUByte().toLong() and 0b10000000) != 0L
                    if (isCopy) {
                        var dataPtr: Int = contentReader.head + 1
                        var offset: Long = 0
                        var size: Long = 0
                        for (i in 0 until 4) {
                            if ((content[contentReader.head].toUByte().toLong() and (1L shl i)) != 0L) {
                                offset = offset or (content[dataPtr++].toUByte().toLong() shl (i * 8))
                            }
                        }
//
//                        println("-1--1 ${content[contentReader.head].toHexString()}")
//                        println("${offset.toString(2)} | $offset")
//                        println("${size.toString(2)} | $size")

                        for (i in 0 until 3) {
                            if ((content[contentReader.head].toUByte().toLong() and (1L shl (i + 4))) != 0L) {
                                size = size or (content[dataPtr++].toUByte().toLong() shl (i * 8))
                            }
                        }
                        contentReader.head = dataPtr

//                        println("-0--0")
//                        println("${offset.toString(2)} | $offset")
//                        println("${size.toString(2)} | $size")
                        val contentStart: Int = obj.findContentStart()
                        val nextContent: ByteArray = obj.bytes.sliceArray(contentStart + offset.toInt() until contentStart + (offset + size).toInt())
                        targetContent = targetContent?.let { it + nextContent } ?: nextContent
                    }
                    else {
                        val size = content[contentReader.head].toInt()
                        val append = content.sliceArray(contentReader.head + 1 until contentReader.head + size + 1)
                        contentReader.head += size + 1
                        targetContent = targetContent?.let { it + append } ?: append
                    }
                }

                val gitObject = createGitObject(obj.type, targetContent!!, targetContent.size)
                writeObject(gitObject)
            }
            GitObject.Type.OFS_DELTA -> {
                TODO()
//                    var deltaOffset: Long = 0
//                    var deltaReadMore: Boolean = true
//                    var deltaPart: Int = 0
//                    while (deltaReadMore) {
//                        val deltaHeader = parseObjHeaderNextByte(content[head++])
//                        deltaReadMore = deltaHeader.readMore
//                        // ?
//                        deltaOffset += deltaHeader.size shl (4 + (deltaPart++ * 7))
//                    }
            }

            GitObject.Type.NONE,
            GitObject.Type.UNUSED -> throw IllegalStateException(objectType.name)
        }
    }

    private fun renderCommit(ref: String) {
        val commit: GitObject = readObject(ref)
        check(commit.type == GitObject.Type.COMMIT)

        val commitContentStart: Int = commit.findContentStart()
        val treeRef: String = commit.bytes.decodeToString(commitContentStart + 5, commitContentStart + 45)

        renderTree(treeRef, "/home/toaster/Downloads/test/kt".toPath())
    }

    private fun renderTree(treeRef: String, path: Path) {
        val tree: GitObject = readObject(treeRef)
        check(tree.type == GitObject.Type.TREE)

        val bytes: ByteArray = tree.bytes
        var head: Int = tree.findContentStart()

        FileSystem.SYSTEM.createDirectories(path)
        check(FileSystem.SYSTEM.exists(path))

        while (head < bytes.size) {
            val split1: Int = bytes.indexOfOrNull(' '.code.toByte(), head) ?: break
            val split2: Int = bytes.indexOfOrNull(0b0, split1)!!

            val mode: String = bytes.decodeToString(head, split1)
            val name: String = bytes.decodeToString(split1 + 1, split2)

            head = split2 + 1

            val objRef: String = bytes.toHexString(head, head + 20)
            head += 20

            when (mode) {
                "40000" -> {
                    renderTree(objRef, path.resolve(name))
                }
                "100644", "100755" -> {
                    println(objRef)
                    val obj: GitObject = readObject(objRef)
                    FileSystem.SYSTEM.write(path.resolve(name)) {
                        val start: Int = obj.findContentStart()
                        write(obj.bytes, start, obj.bytes.size - start)
                    }
                }
                else -> throw NotImplementedError("$mode ($name)")
            }

            println(mode)
            println(name)

        }
    }
}

data class GitObject(val bytes: ByteArray, val type: Type, val hash: String) {
    fun findContentStart(): Int =
        bytes.indexOfOrNull(0b0)!! + 1

    enum class Type {
        NONE,
        COMMIT,
        TREE,
        BLOB,
        TAG,
        UNUSED,
        OFS_DELTA,
        REF_DELTA;

        val identifier: String
            get() = when (this) {
                COMMIT -> "commit"
                TREE -> "tree"
                BLOB -> "blob"
                TAG -> "tag"
                OFS_DELTA -> "ofs_delta"
                REF_DELTA -> "ref_delta"

                NONE,
                UNUSED -> throw IllegalStateException(name)
            }
    }
}

infix fun Byte.and(other: Long): Long =
    toLong() and other

infix fun Byte.shl(bitCount: Int): Long =
    toLong() shl bitCount

fun ByteArray.toInt(): Int =
    ByteBuffer.wrap(this, 0,  size).order(java.nio.ByteOrder.BIG_ENDIAN).getInt()

fun ByteArray.indexOfOrNull(byte: Byte, startIndex: Int = 0, endIndex: Int = size): Int? {
    for (index in startIndex until endIndex) {
        if (this[index] == byte) {
            return index
        }
    }
    return null
}

fun ByteArray.indexOfOrNull(subArray: ByteArray, size: Int? = null): Int? {
    var found: Int = 0
    for (index in 0 until (size ?: this.size)) {
        val byte: Byte = this[index]
        if (byte == subArray[found]) {
            if (++found == subArray.size) {
                return index - subArray.size + 1
            }
        }
        else if (found != 0) {
            found = 0
            if (byte == subArray[0]) {
                return index - subArray.size + 1
            }
        }
    }

    return null
}

private class ByteReader(val bytes: ByteArray, var head: Int, val inflatedBytes: ByteArray)

private fun ByteReader.parseNextFullSize(part: Int): Pair<Boolean, Long> {
    val byte: Byte = bytes[head++]
    val readMore: Boolean = (byte and 0b10000000) != 0L
    val size: Long = (byte and 0b01111111) shl (4 + ((part - 1) * 7))
    return readMore to size
}

private fun ByteReader.parseSizeHeader(partOffset: Int = 0): Int {
    var part: Int = partOffset
    var size: Long = 0

    while (true) {
        val (readMore: Boolean, nextSize: Long) = parseNextFullSize(part++)
        size += nextSize

        if (!readMore) {
            break
        }
    }

    return size.toInt()
}

private val IntRange.size: Int
    get() = last - first + 1

private data class PackFileHeader(val version: Int, val objectCount: Int)

private fun ByteReader.parsePackFileHeader(): PackFileHeader {
    val version: Int = bytes.sliceArray(head until head + 4).toInt()
    head += 4
    val objectCount: Int = bytes.sliceArray(head until head + 4).toInt()
    head += 4

    return PackFileHeader(version, objectCount)
}

private data class SizeAndTypeHeader(val size: Int, val type: GitObject.Type)

private fun ByteReader.parseSizeAndTypeHeader(): SizeAndTypeHeader {
    val byte: Byte = bytes[head++]

    val objTypeIndex: Long = (byte and 0b01110000).toLong() shr 4
    val readMore: Boolean = (byte and 0b10000000) != 0L
    var size: Long = (byte and 0b00001111).toLong()

    if (readMore) {
        size += parseSizeHeader(1)
    }

    val objectType: GitObject.Type =
        try {
            GitObject.Type.entries[objTypeIndex.toInt()]
        }
        catch (e: Throwable) {
            throw RuntimeException("objTypeIndex=$objTypeIndex", e)
        }

    return SizeAndTypeHeader(size.toInt(), objectType)
}

private fun ByteReader.parseContent(expectedSize: Int?): Int {
    val inflater: Inflater = Inflater()
    inflater.setInput(bytes, head, bytes.size - head)

    val actualSize: Int = inflater.inflate(inflatedBytes)

    if (expectedSize != null) {
        check(actualSize == expectedSize) { "Expected: $expectedSize, Actual: $actualSize" }
    }

    head += inflater.bytesRead.toInt()

    return actualSize
}

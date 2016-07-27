package org.apache.flex.utilities.converter.retrievers.download.utils.utils;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by christoferdutz on 25.02.16.
 */
public class DmgPropertyListMishBlock {

    private String blockName;

    private int version;             // Current version is 1
    private long sectorNumber;       // Starting disk sector in this blkx descriptor
    private long sectorCount;        // Number of disk sectors in this blkx descriptor

    private long dataOffset;
    private int buffersNeeded;
    private int blockDescriptors;    // Number of descriptors

    private DmgUdifChecksum checksum;

    private List<DmgBlockChunkEntry> blockChunkEntries;

    public DmgPropertyListMishBlock(String blockName, byte[] data) {
        this.blockName = blockName;

        // Initialize the fields by parsing the input bytes.
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        DataInputStream dis = new DataInputStream(bis);
        try {
            dis.readInt();
            version = dis.readInt();
            sectorNumber = dis.readLong();
            sectorCount = dis.readLong();
            dataOffset = dis.readLong();
            buffersNeeded = dis.readInt();
            blockDescriptors = dis.readInt();
            dis.skipBytes(24);
            byte[] checksumData = new byte[136];
            dis.read(checksumData, 0, 136);
            checksum = new DmgUdifChecksum(checksumData);
            int numberOfBlockChunks = dis.readInt();

            blockChunkEntries = new ArrayList<DmgBlockChunkEntry>();
            byte[] blockChunkData = new byte[40];
            for(int i = 0; i < numberOfBlockChunks; i++) {
                dis.read(blockChunkData);
                DmgBlockChunkEntry entry = new DmgBlockChunkEntry(blockChunkData);
                // The block with the type "0xFFFFFFFF" is the end-block and contains
                // no data we could need, so we simply end here.
                if(entry.getEntryType() == 0xFFFFFFFF) {
                    break;
                }
                blockChunkEntries.add(entry);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid DmgPropertyListMishBlock data.");
        }
    }

    public String getBlockName() {
        return blockName;
    }

    public int getVersion() {
        return version;
    }

    public long getSectorNumber() {
        return sectorNumber;
    }

    public long getSectorCount() {
        return sectorCount;
    }

    public long getDataOffset() {
        return dataOffset;
    }

    public int getBuffersNeeded() {
        return buffersNeeded;
    }

    public int getBlockDescriptors() {
        return blockDescriptors;
    }

    public DmgUdifChecksum getChecksum() {
        return checksum;
    }

    public List<DmgBlockChunkEntry> getBlockChunkEntries() {
        return blockChunkEntries;
    }

}

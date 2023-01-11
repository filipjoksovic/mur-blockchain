package blockchain;

import client.ClientSocketHandler;

import java.util.Random;

public class BlockMinerThread extends Thread {

    BlockUtils blockUtils;
    Random random;

    ClientSocketHandler csh;

    public BlockMinerThread(ClientSocketHandler csh) {
        this();
        this.csh = csh;
    }

    public BlockMinerThread() {
        blockUtils = new BlockUtils();
        random = new Random();
    }

    public void run() {
        synchronized (this) {
            while (true) {
                Block minedBlock = blockUtils.addBlock(String.valueOf(random.nextInt(5000)));
                if (blockUtils.validateChain()) {
                    System.out.println("[INFO]: Blockchain still valid.");
                    System.out.println("[INFO]: Cummulative difficulty: " + blockUtils.calculateCumulativeDifficulty());
                    csh.sendMessageToServers(blockUtils.serializeBlockchain());
                } else {
                    System.out.println("[ERROR]: Blockchain not valid.");
                    return;
                }
                System.out.println("[INFO] Number of blocks: " + blockUtils.getBlockChain().size());
            }
        }
    }

    public String serializeBlock(Block block) {
        return block.getIndex() + "//" + block.getTimestamp() + "//" + block.getData() + "//" + block.getPreviousHash() + "//" + block.getHash() + "//" + block.getNonce() + "//" + this.blockUtils.getDifficulty();
    }
}

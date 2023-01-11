package blockchain;

import client.ClientSocketHandler;
import console.Level;
import console.Logger;

import java.util.Random;

public class BlockMinerThread extends Thread {

    BlockUtils blockUtils;
    Random random;

    ClientSocketHandler csh;
    private final static Logger logger = new Logger(BlockMinerThread.class.getName());

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
                    logger.log("Blockchain still valid.");
                    logger.log("Cummulative difficulty: " + blockUtils.calculateCumulativeDifficulty());
                    csh.sendMessageToServers(blockUtils.serializeBlockchain());
                } else {
                    logger.log(Level.ERROR, "Blockchain not valid.");
//                    return;
                }
                logger.log("Number of blocks: " + blockUtils.getBlockChain().size());
            }
        }
    }

    public String serializeBlock(Block block) {
        return block.getIndex() + "//" + block.getTimestamp() + "//" + block.getData() + "//" + block.getPreviousHash() + "//" + block.getHash() + "//" + block.getNonce() + "//" + this.blockUtils.getDifficulty();
    }
}

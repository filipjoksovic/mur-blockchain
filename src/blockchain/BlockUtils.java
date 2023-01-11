package blockchain;

import java.util.List;
import java.util.Vector;

public class BlockUtils {

    private static List<Block> blockChain = new Vector<>();
    public static List<Block> inMemBlockChain = new Vector<>();
    private int difficulty = 3;
    private int generationPeriod = 10000; //after interval, new block is
    // expected
    private int diffAdjustmentInterval = 10;// after n blocks, difficulty

    // will change

    public List<Block> getBlockChain() {
        return inMemBlockChain;
    }

    public void setBlockChain(List<Block> blockChain) {
        BlockUtils.blockChain = blockChain;
    }

    private void createGenesisBlock() {
        inMemBlockChain.add(new Block(0, "0", "Hello world!"));
    }

    private Block getLatestBlock() {
        if (inMemBlockChain.isEmpty()) {
            createGenesisBlock();
        }
        return inMemBlockChain.get(inMemBlockChain.size() - 1);
    }

    public Block addBlock(String data) {
        Block previousBlock = this.getLatestBlock();
        this.difficulty = previousBlock.getDifficulty();

        Block newBlock = new Block(previousBlock.getIndex() + 1,
                previousBlock.getHash(), data);
        if (inMemBlockChain.size() % diffAdjustmentInterval == 0 && inMemBlockChain.size() >= diffAdjustmentInterval) {
            Block previousAdjustmentBlock =
                    inMemBlockChain.get(inMemBlockChain.size() - diffAdjustmentInterval);
            Long timeExpected = (long) ((long) generationPeriod * diffAdjustmentInterval);
            Long timeTaken =
                    previousBlock.getTimestamp() - previousAdjustmentBlock.getTimestamp();
            System.out.println("[INFO]: Time taken for mining: " + timeTaken);
            if (timeTaken < (timeExpected / 2)) {
                System.out.println("[INFO]: Increasing difficulty: " + this.difficulty + " -> " + (++this.difficulty));

            } else if (timeTaken > (timeExpected * 2)) {
                System.out.println("[INFO]: Decreasing difficulty: " + this.difficulty + " -> " + (--this.difficulty));
            } else {
                System.out.println("[INFO]: Keeping original difficulty: " + difficulty);
            }
        }
        newBlock.setDifficulty(this.difficulty);
        newBlock.mineBlock(this.difficulty);
        if (newBlock.getTimestamp() <= (System.currentTimeMillis() - 60000) || newBlock.getTimestamp() >= previousBlock.getTimestamp() - 60000) {
            blockChain.add(newBlock);
            inMemBlockChain.add(newBlock);
            System.out.println("Blockchain size: " + inMemBlockChain.size());
            return newBlock;
        } else {
            System.out.println("[INFO]:Block not valid");
            return null;
        }
    }

    public boolean validateChain() {
        for (int i = 1; i < inMemBlockChain.size(); i++) {
            Block current = inMemBlockChain.get(i);
            Block previous = inMemBlockChain.get(i - 1);
            if (!current.getHash().equals(current.calculateHash())) {
                return false;
            }
            if (!current.getPreviousHash().equals(previous.calculateHash())) {
                return false;
            }
        }
        return true;
    }

    public boolean validateInMemChain() {
        for (int i = 1; i < inMemBlockChain.size(); i++) {
            Block current = inMemBlockChain.get(i);
            Block previous = inMemBlockChain.get(i - 1);
            if (!current.getHash().equals(current.calculateHash())) {
                return false;
            }
            if (!current.getPreviousHash().equals(previous.calculateHash())) {
                return false;
            }
        }
        return true;
    }

    public static boolean validateChain(List<Block> blockChain) {
        for (int i = 1; i < blockChain.size(); i++) {
            Block current = blockChain.get(i);
            Block previous = blockChain.get(i - 1);
            if (!current.getHash().equals(current.calculateHash())) {
                return false;
            }
            if (!current.getPreviousHash().equals(previous.calculateHash())) {
                return false;
            }
        }
        return true;
    }

    public int calculateCumulativeDifficulty() {
        int sum = 0;
        for (Block block : inMemBlockChain) {
            sum += Math.pow(2, block.getDifficulty());
        }
        return sum;
    }

    public static int calculateCumulativeDifficulty(List<Block> blockChain) {
        int sum = 0;
        for (Block block : blockChain) {
            sum += Math.pow(2, block.getDifficulty());
        }
        return sum;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public void setGenerationPeriod(int generationPeriod) {
        this.generationPeriod = generationPeriod;
    }

    public int getGenerationPeriod() {
        return generationPeriod;
    }

    public String serializeBlockchain() {
        StringBuilder serializedBC = new StringBuilder();
        for (int i = 0; i < inMemBlockChain.size(); i++) {
            serializedBC.append(BlockUtils.serializeBlock(inMemBlockChain.get(i)));
            if (i != inMemBlockChain.size() - 1) {
                serializedBC.append("::");
            }
        }
        return serializedBC.toString();
    }

    public static String serializeBlockchain(List<Block> blockChain) {
        StringBuilder serializedBC = new StringBuilder();
        for (int i = 0; i < blockChain.size(); i++) {
            serializedBC.append(BlockUtils.serializeBlock(blockChain.get(i)));
            if (i != blockChain.size() - 1) {
                serializedBC.append("::");
            }
        }
        return serializedBC.toString();
    }

    public static List<Block> deserializeBlockchain(String message) {
        String[] blockchain = message.split("::");
        Vector<Block> ffs = new Vector<>();
        for (String block : blockchain) {
            Block newBlock = BlockUtils.deserializeBlock(block);
            ffs.add(newBlock);
        }
//        int difficulty = Integer.parseInt(blockchainData[0]);
        return ffs;
    }

    public static String serializeBlock(Block block) {
        return block.getIndex() + "//" + block.getTimestamp() + "//" + block.getData() + "//" + block.getPreviousHash() + "//" + block.getHash() + "//" + block.getNonce() + "//" + block.getDifficulty();
    }

    public static Block deserializeBlock(String message) {
        return new Block(message.split("//"));
    }
}

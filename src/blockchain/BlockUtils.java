package blockchain;

import java.util.ArrayList;
import java.util.List;

public class BlockUtils {

    private List<Block> blockChain = new ArrayList<>();
    private int difficulty = 3;
    private int generationPeriod = 10000; //after interval, new block is
    // expected
    private int diffAdjustmentInterval = 10;// after n blocks, difficulty
    // will change

    public List<Block> getBlockChain() {
        return blockChain;
    }

    public void setBlockChain(List<Block> blockChain) {
        this.blockChain = blockChain;
    }

    private void createGenesisBlock() {
        blockChain.add(new Block(0, "0", "Hello world!"));
    }

    private Block getLatestBlock() {
        if (blockChain.isEmpty()) {
            createGenesisBlock();
        }
        return blockChain.get(blockChain.size() - 1);
    }

    public void addBlock(String data) {
        Block previousBlock = this.getLatestBlock();


        Block newBlock = new Block(previousBlock.getIndex() + 1,
                previousBlock.getHash(), data);
        if (blockChain.size() % diffAdjustmentInterval == 0 && blockChain.size() >= diffAdjustmentInterval) {
            Block previousAdjustmentBlock =
                    blockChain.get(blockChain.size() - diffAdjustmentInterval);
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
        newBlock.mineBlock(this.difficulty);
        blockChain.add(newBlock);
    }

    public boolean validateChain() {
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
}

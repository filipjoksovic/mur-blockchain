package blockchain;

import java.util.ArrayList;
import java.util.List;

public class BlockUtils {

    private List<Block> blockChain = new ArrayList<>();

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
}

public class Block {
    private int health;
    private BlockState state;

    public Block(BlockState state){
        switch(state){
            case ORE -> this.health = Constants.MapConstants.ORE_HEALTH;
            case STONE -> this.health = Constants.MapConstants.STONE_HEALTH;
            case AIR -> this.health = 0;
        }

        this.state = state;
    }

    public BlockState getState(){return this.state;}
    public int getHealth(){return this.health;}


    public void takeDamage(int damage){
        if (state == BlockState.AIR) return;

        health -= damage;
        if(health <= 0){
            health = 0;
            state = BlockState.AIR;
        }
    }
}

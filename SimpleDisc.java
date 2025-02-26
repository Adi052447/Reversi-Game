public class SimpleDisc implements Disc{
    private Player owner;
    public SimpleDisc(Player owner1){this.owner=owner1;
    }
    @Override
    public Player getOwner() {
        return this.owner;
    }

    @Override
    public void setOwner(Player player) {
this.owner=player;
    }

    @Override
    public String getType() {
        return "⬤";
    }
}

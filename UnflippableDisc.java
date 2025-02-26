public class UnflippableDisc implements Disc{
private Player owner;
public UnflippableDisc(Player owner1) {
   this.owner=owner1 ;
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
        return "⭕";
    }
}




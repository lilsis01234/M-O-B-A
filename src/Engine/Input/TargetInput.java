package Engine.Input;
//Interface pour gérer une cible 
public interface TargetInput {
    boolean hasTarget();
    int getTargetX();//position x
    int getTargetY(); //position y
    void clearTarget();//suppression
}


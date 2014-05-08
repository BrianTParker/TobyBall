package com.game.TobyBall;

import project_sanity.essence.AEssence;

/**
 * Health system for TobyBall. 
 * 
 * @author nicholas
 */
public class Health extends AEssence {
    
    private int MIN;
    private int MAX;

    public Health() {
    }

    public Health( int inMin, int inMax ) {
        
        MIN = inMin;
        MAX = inMax;
    }

    @Override
    public int getValue() {
        return MAX - super.getValue();
    }
}

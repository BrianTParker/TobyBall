package com.game.TobyBall;

import project_sanity.essence.AEssence;
import project_sanity.range.Range;

/**
 * Health system for TobyBall. 
 * 
 * @author nicholas
 */
public class Health extends AEssence {
    
    private Range m_range;

    public Health( int inMin, int inMax ) {
        
        m_range = new Range( inMin, inMax );
    }

    @Override
    public int getValue() {
        return m_range.getMAX() - super.getValue();
    }
}

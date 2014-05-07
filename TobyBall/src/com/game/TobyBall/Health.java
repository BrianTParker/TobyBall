package com.game.TobyBall;

import project_sanity.essence.AEssence;
import project_sanity.range.Range;

/**
 * Health system for TobyBall. 
 * 
 * @author nicholas
 */
public class Health extends AEssence {
    
    private final Range m_range;

    public Health( int aMin, int aMax ) {
        
        m_range = new Range( aMin, aMax );
    }

    @Override
    public int getValue() {
        return m_range.getMAX() - super.getValue();
    }
}

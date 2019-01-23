package com.unknownroads.game.tools;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

public class PlayerContactListener implements ContactListener {

    TimeManager tm;
    Sound sound;

    public PlayerContactListener(TimeManager tm, Sound objectiveSound){
        this.tm = tm;
        this.sound = objectiveSound;
        sound.setVolume(-1,0.5f);
    }

    @Override
    public void beginContact(Contact contact) {

        String nameA = (String) contact.getFixtureA().getBody().getUserData();
        String nameB = (String) contact.getFixtureB().getBody().getUserData();

        if((nameA == "goal" || nameB =="goal") && (nameA == "player" ||nameB == "player") ) {
             tm.newLap();
             sound.play();
        }


        if((nameA == "wall" || nameB =="wall") && (nameA == "player" ||nameB == "player") ){
             Gdx.input.vibrate(100);
        }


    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}

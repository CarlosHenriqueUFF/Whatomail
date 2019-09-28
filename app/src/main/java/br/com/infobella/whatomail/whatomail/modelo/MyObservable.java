package br.com.infobella.whatomail.whatomail.modelo;

import java.util.Observable;

/**
 * Created by Henrique on 20/07/2016.
 */
public class MyObservable extends Observable{

    public MyObservable() {

    }

    public void notifyMyObservers(String msg){
        this.notifyObservers(msg);
    }

    public void setChangedObservable(){
        this.setChanged();
    }

}

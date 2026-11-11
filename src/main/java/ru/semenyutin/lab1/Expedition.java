package ru.semenyutin.lab1;

import java.util.*;
import java.util.random.RandomGenerator;

public final class Expedition {
    public enum Event { CACHE, RELIC, EMPTY, STORM, BEACON }
    public enum Route {
        ORBIT("Орбита",45,8,28,9,2,5),
        CRATER("Кратер",36,15,17,22,3,8),
        RIFT("Разлом",28,20,8,34,4,12);
        private final String name;
        private final int cache,relic,empty,storm,small,large;
        Route(String name,int cache,int relic,int empty,int storm,int small,int large) {
            this.name=name;this.cache=cache;this.relic=relic;this.empty=empty;this.storm=storm;this.small=small;this.large=large;
        }
        Event roll(int value) {
            if(value<cache)return Event.CACHE;
            if((value-=cache)<relic)return Event.RELIC;
            if((value-=relic)<empty)return Event.EMPTY;
            if((value-=empty)<storm)return Event.STORM;
            return Event.BEACON;
        }
        public String toString(){return name;}
    }
    record Request(Event event,Route route,long cargo) {}
    record Outcome(long cargo,boolean free,String message) {}
    // Each handler owns one event; unhandled requests are passed to the next link.
    abstract static class Handler {
        private final Event type;
        private final Handler next;
        Handler(Event type,Handler next){this.type=type;this.next=next;}
        final Outcome handle(Request r){
            if(r.event()==type)return apply(r);
            if(next!=null)return next.handle(r);
            throw new IllegalStateException("Событие не обработано: "+r.event());
        }
        abstract Outcome apply(Request r);
    }
    static final class CacheHandler extends Handler {
        CacheHandler(Handler next){super(Event.CACHE,next);}
        Outcome apply(Request r){return new Outcome(r.cargo()+r.route().small,false,"Найден контейнер: +"+r.route().small);}
    }
    static final class RelicHandler extends Handler {
        RelicHandler(Handler next){super(Event.RELIC,next);}
        Outcome apply(Request r){return new Outcome(r.cargo()+r.route().large,false,"Найден артефакт: +"+r.route().large);}
    }
    static final class EmptyHandler extends Handler {
        EmptyHandler(Handler next){super(Event.EMPTY,next);}
        Outcome apply(Request r){return new Outcome(r.cargo(),false,"Сектор пуст; груз сохранён.");}
    }
    static final class StormHandler extends Handler {
        StormHandler(Handler next){super(Event.STORM,next);}
        Outcome apply(Request r){return new Outcome(0,false,"Шторм уничтожил незакреплённый груз: "+r.cargo());}
    }
    static final class BeaconHandler extends Handler {
        BeaconHandler(){super(Event.BEACON,null);}
        Outcome apply(Request r){return new Outcome(r.cargo(),true,"Маяк дал бесплатный следующий полёт.");}
    }
    private final Handler chain=new CacheHandler(new RelicHandler(new EmptyHandler(new StormHandler(new BeaconHandler()))));
    private final RandomGenerator rng;
    private long energy,cargo;
    private boolean free;
    private int flights;
    public Expedition(RandomGenerator rng){this.rng=Objects.requireNonNull(rng);reset();}
    public void reset(){energy=8;cargo=0;free=false;flights=0;}
    public boolean canFly(){return energy>0||free;}
    public long energy(){return energy;}
    public long cargo(){return cargo;}
    public boolean free(){return free;}
    public int flights(){return flights;}
    public String fly(Route route){
        Objects.requireNonNull(route);
        if(!canFly())throw new IllegalStateException("Нет энергии. Сдайте груз или начните новую экспедицию.");
        Outcome o=chain.handle(new Request(route.roll(rng.nextInt(100)),route,cargo));
        if(!free)energy--;
        cargo=o.cargo();free=o.free();flights++;
        return route+" · "+o.message();
    }
    public long dock(){long delivered=cargo;energy+=cargo;cargo=0;return delivered;}
}

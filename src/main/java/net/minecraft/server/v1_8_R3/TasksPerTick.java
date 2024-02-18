package net.minecraft.server.v1_8_R3;

public class TasksPerTick implements Runnable {
    private final int tick;
    private final Runnable task;
    
    public TasksPerTick(int creationTicks, Runnable task) {
        this.tick = creationTicks;
        this.task = task;
    }
    
    public int getTick() {
        return tick;
    }
    
    @Override
    public void run() {
        task.run();
    }
}
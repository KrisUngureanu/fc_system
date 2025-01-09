package com.cifs.or2.client;

public class TaskTableFactory {
    private TaskTableInterface mgr_;
    private static TaskTableFactory inst_;

    public static TaskTableFactory instance() {
        if (inst_ == null)
            inst_ = new TaskTableFactory();
        return inst_;
    }

    public void register(TaskTableInterface mgr) {
        mgr_ = mgr;
    }

    public TaskTableInterface getTaskTable() {
        return mgr_;
    }

    private TaskTableFactory() {
    };
}

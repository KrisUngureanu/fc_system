package kz.tamur.comps.ui.ext;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import kz.tamur.comps.ui.ext.utils.SwingUtils;
import kz.tamur.rt.Utils;

/**
 * @author Sergey Lebedev
 *         <p/>
 *         Класс реализует минимальную замену стандартного класса javax.swing.Timer.
 *         Вместо того, чтобы работать в единственной очереди, класс создаёт отдельные потоки для каждого таймера и не влияет на вызывающий поток.
 *         В целом это означает что можно использовать любое количесво экземпляров этого класса и выполнять их всех сразу, не имея проблем.
 *         <p/>
 *         Также, эта реализация Таймера предлагает множество дополнительных функций и улучшений, которые не имеет стандартный Таймер (например, Вы можете диспетчеризировать события в отдельных не-EDT потоках, в результате чего, избегать использования EDT вообще).
 */

public class Timer {

    /** The default name. */
    public static String defaultName = "Timer";

    // Timer event listeners
    /** The listeners. */
    private List<ActionListener> listeners = new ArrayList<ActionListener>();

    // Runtime variables
    /** The id. */
    private int id = 0;

    /** The last id. */
    private int lastId;

    /** The running. */
    private Map<Integer, Boolean> running = new Hashtable<Integer, Boolean>();

    /** The sleep start. */
    private long sleepStart = 0;

    /** The sleep time. */
    private long sleepTime = 0;

    /** The exec. */
    private Thread exec = null;

    // Swing Timer settings
    /** The delay. */
    private long delay;

    /** The initial delay. */
    private long initialDelay;

    /** The repeats. */
    private boolean repeats = true;

    /** The coalesce. */
    private boolean coalesce = true;

    /** The use event dispatch thread. */
    private boolean useEventDispatchThread = true;

    /** The action command. */
    private String actionCommand = "";

    /** The name. */
    private String name = null;

    // Timer cycles execution limit, 0 and less - unlimited
    /** The cycles limit. */
    private int cyclesLimit = 0;

    /**
     * Создание нового timer.
     * 
     * @param delay
     *            the delay
     */
    public Timer(long delay) {
        this(defaultName, delay);
    }

    /**
     * Создание нового timer.
     * 
     * @param name
     *            the name
     * @param delay
     *            the delay
     */
    public Timer(String name, long delay) {
        this(name, delay, null);
    }

    /**
     * Создание нового timer.
     * 
     * @param delay
     *            the delay
     * @param initialDelay
     *            the initial delay
     */
    public Timer(long delay, long initialDelay) {
        this(defaultName, delay, initialDelay);
    }

    /**
     * Создание нового timer.
     * 
     * @param name
     *            the name
     * @param delay
     *            the delay
     * @param initialDelay
     *            the initial delay
     */
    public Timer(String name, long delay, long initialDelay) {
        this(name, delay, initialDelay, null);
    }

    /**
     * Создание нового timer.
     * 
     * @param delay
     *            the delay
     * @param listener
     *            the listener
     */
    public Timer(long delay, ActionListener listener) {
        this(defaultName, delay, listener);
    }

    /**
     * Создание нового timer.
     * 
     * @param name
     *            the name
     * @param delay
     *            the delay
     * @param listener
     *            the listener
     */
    public Timer(String name, long delay, ActionListener listener) {
        this(name, delay, -1, listener);
    }

    /**
     * Создание нового timer.
     * 
     * @param delay
     *            the delay
     * @param initialDelay
     *            the initial delay
     * @param listener
     *            the listener
     */
    public Timer(long delay, long initialDelay, ActionListener listener) {
        this(defaultName, delay, initialDelay, listener);
    }

    /**
     * Создание нового timer.
     * 
     * @param name
     *            the name
     * @param delay
     *            the delay
     * @param initialDelay
     *            the initial delay
     * @param listener
     *            the listener
     */
    public Timer(String name, long delay, long initialDelay, ActionListener listener) {
        super();
        setName(name);
        setDelay(delay);
        setInitialDelay(initialDelay);
        addActionListener(listener);
    }

    /**
     * Initial timer delay.
     * 
     * @return the initial delay
     */

    public long getInitialDelay() {
        return initialDelay;
    }

    /**
     * Установить initial delay.
     * 
     * @param initialDelay
     *            the new initial delay
     */
    public void setInitialDelay(long initialDelay) {
        if (initialDelay != -1 && initialDelay < 0) {
            throw new IllegalArgumentException("Invalid initial delay: " + initialDelay);
        } else {
            this.initialDelay = initialDelay;
        }
    }

    /**
     * Timer delay.
     * 
     * @return the delay
     */

    public long getDelay() {
        return delay;
    }

    /**
     * Установить delay.
     * 
     * @param delay
     *            the new delay
     */
    public void setDelay(long delay) {
        if (delay < 0) {
            throw new IllegalArgumentException("Invalid delay: " + delay);
        } else {
            this.delay = delay;
        }
    }

    /**
     * FPS-based delay.
     * 
     * @param fps
     *            the new fps
     */

    public void setFPS(int fps) {
        if (fps < 1) {
            throw new IllegalArgumentException("Invalid FPS: " + fps);
        } else {
            this.delay = 1000 / fps;
        }
    }

    /**
     * Получить fps.
     * 
     * @return the fps
     */
    public int getFPS() {
        return Math.round(1000 / getDelay());
    }

    /**
     * Repeating timer.
     * 
     * @return true, если repeats
     */

    public boolean isRepeats() {
        return repeats;
    }

    /**
     * Установить repeats.
     * 
     * @param repeats
     *            the new repeats
     */
    public void setRepeats(boolean repeats) {
        this.repeats = repeats;
    }

    /**
     * Coalesce events threads.
     * 
     * @return true, если coalesce
     */

    public boolean isCoalesce() {
        return coalesce;
    }

    /**
     * Установить coalesce.
     * 
     * @param coalesce
     *            the new coalesce
     */
    public void setCoalesce(boolean coalesce) {
        this.coalesce = coalesce;
    }

    /**
     * Perform timer action in EDT or not.
     * 
     * @return true, если use event dispatch thread
     */

    public boolean isUseEventDispatchThread() {
        return useEventDispatchThread;
    }

    /**
     * Установить use event dispatch thread.
     * 
     * @param useEventDispatchThread
     *            the new use event dispatch thread
     */
    public void setUseEventDispatchThread(boolean useEventDispatchThread) {
        this.useEventDispatchThread = useEventDispatchThread;
    }

    /**
     * Action command.
     * 
     * @return the action command
     */

    public String getActionCommand() {
        return actionCommand;
    }

    /**
     * Установить action command.
     * 
     * @param actionCommand
     *            the new action command
     */
    public void setActionCommand(String actionCommand) {
        this.actionCommand = actionCommand;
    }

    /**
     * Timer cycles limit.
     * 
     * @return the cycles limit
     */

    public int getCyclesLimit() {
        return cyclesLimit;
    }

    /**
     * Установить cycles limit.
     * 
     * @param cyclesLimit
     *            the new cycles limit
     */
    public void setCyclesLimit(int cyclesLimit) {
        this.cyclesLimit = cyclesLimit;
    }

    /**
     * Timer thread name.
     * 
     * @return the name
     */

    public String getName() {
        return name;
    }

    /**
     * Установить name.
     * 
     * @param name
     *            the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Time passed since last timer action.
     * 
     * @return the cycle time passed
     */

    public long getCycleTimePassed() {
        return System.currentTimeMillis() - sleepStart;
    }

    /**
     * Time left until next timer action.
     * 
     * @return the cycle time left
     */

    public long getCycleTimeLeft() {
        return sleepTime - getCycleTimePassed();
    }

    /**
     * Timer ations.
     * 
     * @param listener
     *            the listener
     */

    public void addActionListener(ActionListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    /**
     * Removes the action listener.
     * 
     * @param listener
     *            the listener
     */
    public void removeActionListener(ActionListener listener) {
        if (listener != null) {
            listeners.remove(listener);
        }
    }

    /**
     * Получить listeners.
     * 
     * @return the listeners
     */
    public List<ActionListener> getListeners() {
        return listeners;
    }

    /**
     * Timer activity actions.
     */

    public void start() {
        startExec();
    }

    /**
     * Stop.
     */
    public void stop() {
        stopExec();
    }

    /**
     * Restart.
     */
    public void restart() {
        stopExec();
        startExec();
    }

    /**
     * Проверяет, является ли running.
     * 
     * @return true, если running
     */
    public synchronized boolean isRunning() {
        return exec != null && exec.isAlive();
        // return running.values ().contains ( true );
    }

    /**
     * Timer logic.
     */

    private synchronized void startExec() {
        // Ignore if timer is already running
        if (isRunning()) {
            return;
        }

        // Saving current thread unique id
        lastId = id;
        id++;

        // Starting new cycling thread
        final int currentId = lastId;
        exec = new Thread(new Runnable() {
            public void run() {
                // Adding a live thread into map
                setAlive(currentId, true);

                try {
                    // Initial delay
                    long actualInitialDelay = getInitialDelay() < 0 ? getDelay() : getInitialDelay();
                    if (actualInitialDelay > 0) {
                        sleepStart = System.currentTimeMillis();
                        sleepTime = actualInitialDelay;
                        Thread.sleep(actualInitialDelay);
                    }

                    // Checking if we sould stop execution
                    if (shouldContinue(-1, currentId)) {
                        // Starting cycles execution
                        if (repeats) {
                            // Repeated events
                            int cycle = 0;
                            while (shouldContinue(cycle, currentId)) {
                                // Firing events
                                fireEvent();

                                // Incrementing cycles count
                                cycle++;

                                // Checking if we sould stop execution due to changes through events
                                if (!shouldContinue(cycle, currentId)) {
                                    break;
                                }

                                // Waiting for next execution
                                if (getDelay() > 0) {
                                    long currentDelay = getDelay();
                                    sleepStart = System.currentTimeMillis();
                                    sleepTime = currentDelay;
                                    Thread.sleep(currentDelay);
                                }
                            }
                        } else {
                            // Single event
                            fireEvent();
                        }
                    }
                } catch (InterruptedException e) {
                    // Execution interrupted
                }

                // Removing finished thread from map
                cleanUp(currentId);
            }
        }, name);
        exec.start();
    }

    /**
     * Should continue.
     * 
     * @param cycle
     *            the cycle
     * @param id
     *            the id
     * @return true, в случае успеха
     */
    private boolean shouldContinue(int cycle, int id) {
        return running.get(id) && !Thread.currentThread().isInterrupted() && (cyclesLimit <= 0 || cyclesLimit > cycle);
    }

    /**
     * Sets the alive.
     * 
     * @param id
     *            the id
     * @param alive
     *            the alive
     */
    private void setAlive(int id, boolean alive) {
        running.put(id, alive);
    }

    /**
     * Clean up.
     * 
     * @param id
     *            the id
     */
    private void cleanUp(int id) {
        running.remove(id);
    }

    /**
     * Stop exec.
     */
    private synchronized void stopExec() {
        if (exec != null) {
            // Interrupt thread
            exec.interrupt();

            // Stop execution from inside
            setAlive(lastId, false);

            // Wait for execution to stop
            try {
                exec.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Fire event.
     */
    private void fireEvent() {
        if (listeners.size() > 0) {
            // Event
            final ActionEvent e = createActionEvent();

            // Working with local array
            final List<ActionListener> listenerList = Utils.clone(listeners);

            // Dispatch event in chosen way
            if (useEventDispatchThread) {
                if (coalesce) {
                    // Merge all events into single call to event dispatch thread
                    SwingUtils.invokeAndWaitSafely(new Runnable() {
                        public void run() {
                            for (ActionListener listener : listenerList) {
                                listener.actionPerformed(e);
                            }
                        }
                    });
                } else {
                    // Make separate event calls to event dispatch thread
                    for (final ActionListener listener : listenerList) {
                        SwingUtils.invokeAndWaitSafely(new Runnable() {
                            public void run() {
                                listener.actionPerformed(e);
                            }
                        });
                    }
                }
            } else {
                // Execute events in the same thread with timer
                for (ActionListener listener : listenerList) {
                    listener.actionPerformed(e);
                }
            }
        }
    }

    /**
     * Creates the action event.
     * 
     * @return action event
     */
    private ActionEvent createActionEvent() {
        return new ActionEvent(Timer.this, 0, actionCommand, System.currentTimeMillis(), 0);
    }

    /**
     * Additional useful methods.
     * 
     * @param delay
     *            the delay
     * @param actionListener
     *            the action listener
     * @return timer
     */

    public static Timer doOnce(long delay, ActionListener actionListener) {
        Timer once = new Timer(delay, actionListener);
        once.setRepeats(false);
        once.start();
        return once;
    }

    /**
     * Repeat.
     * 
     * @param delay
     *            the delay
     * @param actionListener
     *            the action listener
     * @return timer
     */
    public static Timer repeat(long delay, ActionListener actionListener) {
        return repeat(delay, delay, actionListener);
    }

    /**
     * Repeat.
     * 
     * @param delay
     *            the delay
     * @param initialDelay
     *            the initial delay
     * @param actionListener
     *            the action listener
     * @return timer
     */
    public static Timer repeat(long delay, long initialDelay, ActionListener actionListener) {
        Timer repeat = new Timer(delay, initialDelay, actionListener);
        repeat.setRepeats(true);
        repeat.start();
        return repeat;
    }
}
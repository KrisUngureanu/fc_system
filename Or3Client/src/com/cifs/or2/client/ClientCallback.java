package com.cifs.or2.client;

import java.awt.Container;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import kz.tamur.comps.Constants;

import com.cifs.or2.kernel.KrnException;
import com.cifs.or2.kernel.MessageNote;
import com.cifs.or2.kernel.Note;
import com.cifs.or2.kernel.ReplChangesProgressNote;
import com.cifs.or2.kernel.ReplFilesProgressNote;
import com.cifs.or2.kernel.ReportNote;
import com.cifs.or2.kernel.ScriptExecResultNote;
import com.cifs.or2.kernel.SystemNote;
import com.cifs.or2.kernel.TaskReloadNote;
import com.cifs.or2.kernel.UserSessionValue;

/**
 * Created by IntelliJ IDEA.
 * User: berik
 * Date: 16.12.2004
 * Time: 11:58:02
 */
public class ClientCallback extends Thread {

    private Kernel krn;
    private Container frame;
	private String typeClient;
    private final static int FEEDBACK_SLEEP_TIMEOUT = 1000;
	private ReplProgressListener replProgressListener;
	private ScriptExecResultListener scriptExecResultListener;
	private ReportConstructorListener rcl;

    public ClientCallback(Kernel krn) {
        this.krn = krn;
    }

    public void setFrame(Container frame) {
        this.frame = frame;
    }

    public void setImportProgressListener(ReplProgressListener importProgressListener) {
		this.replProgressListener = importProgressListener;
	}
    
    public void setScriptExecResultListener(ScriptExecResultListener scriptExecResultListener) {
		this.scriptExecResultListener = scriptExecResultListener;
	}

    public void setReportConstructorListener(ReportConstructorListener rcl) {
		this.rcl = rcl;
	}

    public void run() {
		while(getNotes());
	}
	
	public boolean getNotes() {
		Note[] notes = null;
		try {
    		notes = krn.getNotes();
        	if (notes != null && notes.length == 0) {
		    	try {
		    		Thread.sleep(FEEDBACK_SLEEP_TIMEOUT);
		    	} catch (InterruptedException e) {
		    	}
        	} else if (notes == null) {
        		throw new Throwable("Соединение с сервером утеряно.\nПопытайтесь перегрузить приложение.");
        	}
        } catch (Throwable e) {
        	e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Соединение с сервером утеряно.\nПопытайтесь перегрузить приложение.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        for (Note note : notes) {
            if (note instanceof MessageNote) {
                if (frame != null || Constants.CLIENT_TYPE_REPORT.equals(typeClient)) {
                    final MessageNote mnote = MessageNote.class.cast(note);
                    UserSessionValue us = note.from;
                    final String msg = (us != null)  ? "Сообщение от пользователя '" + us.name + "' с компьютера " + us.pcName + " (" + us.ip + "): \n\n" + mnote.message : mnote.message;

                    EventQueue.invokeLater(new Runnable() {
                        public void run() {
                            if (mnote.isDropUser) {
                            	if (msg == null) {
                            		krn.release();
                            	} else {
                                	showMessage(true);
                            	}
                            } else {
                            	showMessage(false);
                            }
                        }
                        
                        private void showMessage(boolean isLogout) {
                        	if (frame != null) {
                        		JOptionPane.showMessageDialog(frame, msg);
                        	} else {
                        		final JFrame frame = new JFrame();
                        		frame.setAlwaysOnTop(true);  
                        		frame.setVisible(true);
                        		JOptionPane.showMessageDialog(frame, msg);
                        		frame.setVisible(false);
                        	}
                        	if (isLogout) {
                        		krn.release();
                        	}
                        }
                    });
                }
            } else if (note instanceof TaskReloadNote) {
                final TaskReloadNote tnote = TaskReloadNote.class.cast(note);
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        TaskTableInterface taskTable = TaskTableFactory.instance().getTaskTable();
                        if (taskTable != null) {
                            taskTable.taskReload(tnote.flowId, tnote.flowParam);
                        }
                    }
                });
            } else if (note instanceof SystemNote) {
                TaskTableInterface taskTable = TaskTableFactory.instance().getTaskTable();
                if (taskTable != null) {
                    taskTable.doOnNotification((SystemNote) note);
                }
            } else if (note instanceof ReplFilesProgressNote) {
				ReplFilesProgressNote inote = ReplFilesProgressNote.class.cast(note);
				if (replProgressListener != null) {
					replProgressListener.replFilesProgress(inote.type, inote.filesCount, inote.currentFileNumber, inote.currentFileName, inote.importTime);
				}
            } else if (note instanceof ReplChangesProgressNote) {
            	final ReplChangesProgressNote inote = ReplChangesProgressNote.class.cast(note);
				if (replProgressListener != null) {
	    			replProgressListener.replChangesProgress(inote.type, inote.currentChangeNumber, inote.changesCount, inote.changeType, inote.changeId);
				}
            } else if (note instanceof ScriptExecResultNote) {
            	ScriptExecResultNote inote = ScriptExecResultNote.class.cast(note);
				if (scriptExecResultListener != null) {
					scriptExecResultListener.scriptExecResult(inote.resultCode, inote.varsMap, inote.message);
				}
            } else if (note instanceof ReportNote) {
                final ReportNote rnote = ReportNote.class.cast(note);
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        if (rcl != null) {
                            String res = rcl.executeCommand(rnote);
                            try {
								krn.sendMessage(rnote.from.id, res);
							} catch (KrnException e) {
								e.printStackTrace();
							}
                        }
                    }
                });
			}
        }
        return frame != null || Constants.CLIENT_TYPE_REPORT.equals(typeClient);
    }

	public void setTypeClient(String typeClient) {
		this.typeClient = typeClient;
	}
}
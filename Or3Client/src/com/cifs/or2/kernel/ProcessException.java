package com.cifs.or2.kernel;

/**
 * Класс для выявления ошибок происходящих при запуске процесса
 */
public final class ProcessException extends Exception
{
  
  /**
   * Создание нового process exception.
   */
  public ProcessException ()
  {
    super();
  } 

  /**
   * Создание нового process exception.
   *
   * @param _message the _message
   */
  public ProcessException (String _message)
  {
    super(_message);
  }  
} 

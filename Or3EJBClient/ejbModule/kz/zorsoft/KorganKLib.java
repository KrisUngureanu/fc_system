package kz.zorsoft;

public class KorganKLib
{

    static
    {
       //System.loadLibrary("KorganLib");
    }


        public native int  nativeKZ_FindNextDevice(byte[] Serial, int curindex);
        public native int  nativeKZ_SerialTopcSerial(byte[] Serial,byte[] pcSerial);

        public native int  nativeKZ_OpenDevice(byte[] Serial);
        public native int nativeKZ_CloseDevice(int iHandle);
        private native int nativeKZ_GetSerial(int iHandle, byte[] pSerial);

        private native int nativeKZ_SetFlashBytes(int iHandle, int address,  byte[] pSource, int len);
        private native int nativeKZ_GetFlashBytes(int iHandle, int address,  byte[] pDest, int len);

        private native int nativeKZ_GenKeys(int iHandle, byte[] magic, byte[] pstrPassw, int dwPasswLen);
        public native int nativeKZ_ActivateDevice(int iHandle, byte[]pstrPassw, int dwPasswLen, int dwDueTime);
        public native int nativeKZ_DeactivateDevice(int iHandle);

        public native int nativeKZ_HashGost(int iHandle,byte[] pbData, int dwLen, byte[] bHashValue);
        private native int nativeKZ_HashSHA(int iHandle, byte[] pbData, int dwLen, byte[] bHashValue,int hashlen);

        private native int nativeKZ_GetRandom(int iHandle, byte[] pbData, int dwSize);
        private native int nativeKZ_GetNoise(int iHandle, byte[] pbData, int dwSize);

        public native int nativeKZ_SignHash(int iHandle,byte[] pbhashvalue, int hashlen, byte[]  pSign);
        private native int nativeKZ_SignHashByCipherKey(int iHandle,byte[] pbhashvalue, int hashlen, byte[] pSign);
        public native int nativeKZ_NotarizeHash(int iHandle, byte[] pbhashvalue, int hashlen, byte[] pSign, byte[] pubkey);

        private native int nativeKZ_Encrypt(int iHandle, byte[] pInBuf, byte[] pOutBuf,int len,
                             byte[] pubkey, byte[] decryptinfo);
        private native int nativeKZ_Decrypt(int iHandle, byte[] pInBuf, byte[] pOutBuf, int len,
                             byte[] decryptinfo);
                             
        public native int nativeKZ_ExportSignY(int iHandle, byte[] pubkey);
        private native int nativeKZ_ExportCypherY(int iHandle, byte[] pubkey);



        private native int nativeKZ_SetNewPassword(int iHandle, byte[] lpbPass, int LenPass);
        public native String nativeKZ_GetLastErrorMsg(int dwGetLastErrorCode);
        public native int nativeKZ_GetLastLibraryError(int iHandle);
        private native int nativeKZ_GetStatus(int iHandle);








	public static void main(String[] args)
	{
             byte Serial[] =  new byte[8];;
             byte pcSerial[] =  new byte[32];;
              boolean IsFound=false;
              int curindex=0;



              KorganKLib KorganK=new KorganKLib();

              //Find new device
	       	while(KorganK.nativeKZ_FindNextDevice(Serial,curindex) != 0)
                 {
                  KorganK.nativeKZ_SerialTopcSerial( Serial, pcSerial);
                  String value = new String(pcSerial);

                  System.out.println("Serial =  "+value+"  Index-"+curindex);
                  IsFound=true;
                  curindex=curindex+1;
                 }
               if(IsFound==false)
               {
                 System.out.println("Device not found");
                 return;
               }

                System.out.println("--------------------------------");
                System.out.println("Open Device #1");

                KorganK.nativeKZ_FindNextDevice(Serial,0);
                int DeviceHandle = KorganK.nativeKZ_OpenDevice(Serial);

                if(DeviceHandle == -1)
                {
                  System.out.println(KorganK.nativeKZ_GetLastErrorMsg( KorganK.nativeKZ_GetLastLibraryError(DeviceHandle)));
                  return;
                }

                byte CheckBuffer[] =  new byte[256];;


                System.out.println("--------------------------------");
                System.out.println("Write 256 bytes.....");
                int retval = KorganK.nativeKZ_SetFlashBytes(DeviceHandle,0, CheckBuffer, 256);
               if(retval == 0)
                {
                  KorganK.nativeKZ_CloseDevice(DeviceHandle);
                  System.out.println(KorganK.nativeKZ_GetLastErrorMsg( KorganK.nativeKZ_GetLastLibraryError(DeviceHandle)));
                  return;
                }



                System.out.println("--------------------------------");
                System.out.println("Read 256 bytes.....");
                retval = KorganK.nativeKZ_GetFlashBytes(DeviceHandle,0, CheckBuffer, 256);
               if(retval == 0)
                {
                  KorganK.nativeKZ_CloseDevice(DeviceHandle);
                  System.out.println(KorganK.nativeKZ_GetLastErrorMsg( KorganK.nativeKZ_GetLastLibraryError(DeviceHandle)));
                  return;
                }



               System.out.println("--------------------------------");
               System.out.println("Get noise 1024bytes");
               byte RandomData[] =  new byte[1024];;

               retval = KorganK.nativeKZ_GetNoise(DeviceHandle, RandomData,1024);
               if(retval == 0)
                {

                  KorganK.nativeKZ_CloseDevice(DeviceHandle);
                  System.out.println(KorganK.nativeKZ_GetLastErrorMsg( KorganK.nativeKZ_GetLastLibraryError(DeviceHandle)));
                  return;
                }


              String stringPD = "1";


               /* Magic = "A028BC243ECAE510";

               System.out.println("--------------------------------");
               System.out.println("Gen Keys");
              retval = KorganK.nativeKZ_GenKeys(DeviceHandle,Magic.getBytes(), stringPassword.getBytes(), 1);
               if(retval == 0)
                {

                  KorganK.nativeKZ_CloseDevice(DeviceHandle);
                  System.out.println(KorganK.nativeKZ_GetLastErrorMsg( KorganK.nativeKZ_GetLastLibraryError(DeviceHandle)));
                  return;
                }
             */


               System.out.println("--------------------------------");
               System.out.println("Activate device in psw:1");
               retval = KorganK.nativeKZ_ActivateDevice(DeviceHandle, stringPD.getBytes(),1, 1);
               if(retval == 0)
                {

                  KorganK.nativeKZ_CloseDevice(DeviceHandle);
                  System.out.println(KorganK.nativeKZ_GetLastErrorMsg( KorganK.nativeKZ_GetLastLibraryError(DeviceHandle)));
                  return;
                }







              byte HashData[] =  new byte[64];;

               System.out.println("--------------------------------");
               System.out.println("Hash Gost");

               retval = KorganK.nativeKZ_HashGost(DeviceHandle,RandomData, 1024, HashData);
               if(retval == 0)
                {

                  KorganK.nativeKZ_CloseDevice(DeviceHandle);
                  System.out.println(KorganK.nativeKZ_GetLastErrorMsg( KorganK.nativeKZ_GetLastLibraryError(DeviceHandle)));
                  return;
                }

              byte pSign[] =  new byte[168];;


               System.out.println("--------------------------------");
               System.out.println("Sign Gost");

               retval = KorganK.nativeKZ_SignHash(DeviceHandle,HashData, 64, pSign);
               if(retval == 0)
                {

                  KorganK.nativeKZ_CloseDevice(DeviceHandle);
                  System.out.println(KorganK.nativeKZ_GetLastErrorMsg( KorganK.nativeKZ_GetLastLibraryError(DeviceHandle)));
                  return;
                }
              System.out.println("--------------------------------");
               System.out.println("Export PubKey");

              byte pubkey[] =  new byte[168];;
              retval = KorganK.nativeKZ_ExportSignY(DeviceHandle,pubkey);
               if(retval == 0)
                {

                  KorganK.nativeKZ_CloseDevice(DeviceHandle);
                  System.out.println(KorganK.nativeKZ_GetLastErrorMsg( KorganK.nativeKZ_GetLastLibraryError(DeviceHandle)));
                  return;
                }





               System.out.println("--------------------------------");
               System.out.println("Verify Gost");

               retval = KorganK.nativeKZ_NotarizeHash(DeviceHandle, HashData,64,  pSign,pubkey);
               if(retval == 0)
                {

                  KorganK.nativeKZ_CloseDevice(DeviceHandle);
                  System.out.println(KorganK.nativeKZ_GetLastErrorMsg( KorganK.nativeKZ_GetLastLibraryError(DeviceHandle)));
                  return;
                }


               System.out.println("--------------------------------");
               System.out.println("Export CypherY PubKey");

              byte CypherPubkey[] =  new byte[168];;
              retval = KorganK.nativeKZ_ExportCypherY(DeviceHandle,CypherPubkey);
               if(retval == 0)
                {

                  KorganK.nativeKZ_CloseDevice(DeviceHandle);
                  System.out.println(KorganK.nativeKZ_GetLastErrorMsg( KorganK.nativeKZ_GetLastLibraryError(DeviceHandle)));
                  return;
                }

              byte decryptinfo[] =  new byte[168];;
              byte EncryotData[] =  new byte[1024];;

               System.out.println("--------------------------------");
               System.out.println("Encrypt data");

              retval = KorganK.nativeKZ_Encrypt(DeviceHandle,RandomData,EncryotData,1024, CypherPubkey,decryptinfo);
               if(retval == 0)
                {

                  KorganK.nativeKZ_CloseDevice(DeviceHandle);
                  System.out.println(KorganK.nativeKZ_GetLastErrorMsg( KorganK.nativeKZ_GetLastLibraryError(DeviceHandle)));
                  return;
                }

              byte OutData[] =  new byte[1024];;

               System.out.println("--------------------------------");
               System.out.println("Decrypt data");

               retval = KorganK.nativeKZ_Decrypt(DeviceHandle,EncryotData,OutData,1024,decryptinfo);
               if(retval == 0)
                {

                  KorganK.nativeKZ_CloseDevice(DeviceHandle);
                  System.out.println(KorganK.nativeKZ_GetLastErrorMsg( KorganK.nativeKZ_GetLastLibraryError(DeviceHandle)));
                  return;
                }
            
              KorganK.nativeKZ_CloseDevice(DeviceHandle);
            
	}
}






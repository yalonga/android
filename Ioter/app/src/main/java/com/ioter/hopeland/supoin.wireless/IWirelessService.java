/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\BackUps\\android\\WirelessTest\\src\\com\\supoin\\wireless\\IWirelessService.aidl
 */
package com.ioter.hopeland.supoin.wireless;
public interface IWirelessService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements IWirelessService
{
private static final String DESCRIPTOR = "com.supoin.wireless.IWirelessService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.supoin.wireless.IWirelessService interface,
 * generating a proxy if needed.
 */
public static IWirelessService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof IWirelessService))) {
return ((IWirelessService)iin);
}
return new Proxy(obj);
}
@Override
public android.os.IBinder asBinder()
{
return this;
}
@Override
public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_open:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.open();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_close:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.close();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_connectTo433:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.connectTo433();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_writeTo433:
{
data.enforceInterface(DESCRIPTOR);
String _arg0;
_arg0 = data.readString();
int _result = this.writeTo433(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getSn:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getSn();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements IWirelessService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override
public android.os.IBinder asBinder()
{
return mRemote;
}
public String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override
public int open() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_open, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override
public int close() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_close, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override
public int connectTo433() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_connectTo433, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override
public int writeTo433(String code) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(code);
mRemote.transact(Stub.TRANSACTION_writeTo433, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override
public int getSn() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getSn, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_open = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_close = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_connectTo433 = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_writeTo433 = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_getSn = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
}
public int open() throws android.os.RemoteException;
public int close() throws android.os.RemoteException;
public int connectTo433() throws android.os.RemoteException;
public int writeTo433(String code) throws android.os.RemoteException;
public int getSn() throws android.os.RemoteException;
}

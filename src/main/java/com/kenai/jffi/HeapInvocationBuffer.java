/*
 * Copyright (C) 2008, 2009 Wayne Meissner
 *
 * This file is part of jffi.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * 
 * Alternatively, you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this work.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.kenai.jffi;

import java.math.BigDecimal;
import java.nio.ByteOrder;

/**
 * An implementation of {@link InvocationBuffer} that packs its parameters onto
 * a java heap allocated buffer.
 */
public final class HeapInvocationBuffer extends InvocationBuffer {
    static final int FFI_SIZEOF_ARG = Platform.getPlatform().addressSize() / 8;
    private static final int PARAM_SIZE = 8;
    static final Encoder encoder = getEncoder();
    private final CallContext callContext;
    private final byte[] buffer;
    private ObjectBuffer objectBuffer;
    private int paramOffset = 0;
    private int paramIndex = 0;

    /**
     * Creates a new instance of <tt>HeapInvocationBuffer</tt>.
     *
     * @param function The function that this buffer is going to be used with.
     */
    public HeapInvocationBuffer(Function function) {
        this.callContext = function.getCallContext();
        buffer = new byte[encoder.getBufferSize(callContext)];
    }

    /**
     * Creates a new instance of <tt>HeapInvocationBuffer</tt>.
     *
     * @param callContext The {@link CallContext} describing how the function should be invoked
     */
    public HeapInvocationBuffer(CallContext callContext) {
        this.callContext = callContext;
        buffer = new byte[encoder.getBufferSize(callContext)];
    }

    /**
     * Creates a new instance of <tt>HeapInvocationBuffer</tt>.
     *
     * @param context The {@link CallContext} describing how the function should be invoked
     */
    public HeapInvocationBuffer(CallContext context, int objectCount) {
        this.callContext = context;
        buffer = new byte[encoder.getBufferSize(context)];
        objectBuffer = new ObjectBuffer(objectCount);
    }


    /**
     * Gets the backing array of this <tt>InvocationBuffer</tt>
     *
     * @return The backing array for this buffer.
     */
    byte[] array() {
        return buffer;
    }

    /**
     * Gets the object buffer used to store java heap array parameters
     *
     * @return An <tt>ObjectBuffer</tt>
     */
    ObjectBuffer objectBuffer() {
        return objectBuffer;
    }
    
    public final void putByte(final int value) {
        paramOffset = encoder.putByte(buffer, paramOffset, value);
        ++paramIndex;
    }

    public final void putShort(final int value) {
        paramOffset = encoder.putShort(buffer, paramOffset, value);
        ++paramIndex;
    }

    public final void putInt(final int value) {
        paramOffset = encoder.putInt(buffer, paramOffset, value);
        ++paramIndex;
    }

    public final void putLong(final long value) {
        paramOffset = encoder.putLong(buffer, paramOffset, value);
        ++paramIndex;
    }

    public final void putFloat(final float value) {
        paramOffset = encoder.putFloat(buffer, paramOffset, value);
        ++paramIndex;
    }

    public final void putDouble(final double value) {
        paramOffset = encoder.putDouble(buffer, paramOffset, value);
        ++paramIndex;
    }

    public final void putLongDouble(final double value) {

        if (encoder.isRaw()) {
            Foreign.getInstance().longDoubleFromDouble(value, buffer, paramOffset, Type.LONGDOUBLE.size());
            paramOffset += FFI_ALIGN(Type.LONGDOUBLE.size(), FFI_SIZEOF_ARG);

        } else {
            byte[] ld = new byte[Type.LONGDOUBLE.size()];
            Foreign.getInstance().longDoubleFromDouble(value, ld, 0, Type.LONGDOUBLE.size());
            getObjectBuffer().putArray(paramIndex, ld, 0, ld.length, ObjectBuffer.IN);
            paramOffset += PARAM_SIZE;
        }
        ++paramIndex;
    }

    public final void putLongDouble(final BigDecimal value) {
        if (encoder.isRaw()) {
            Foreign.getInstance().longDoubleFromString(value.toEngineeringString(), buffer, paramOffset, Type.LONGDOUBLE.size());
            paramOffset += FFI_ALIGN(Type.LONGDOUBLE.size(), FFI_SIZEOF_ARG);

        } else {
            byte[] ld = new byte[Type.LONGDOUBLE.size()];
            Foreign.getInstance().longDoubleFromString(value.toEngineeringString(), ld, 0, Type.LONGDOUBLE.size());
            getObjectBuffer().putArray(paramIndex, ld, 0, ld.length, ObjectBuffer.IN);
            paramOffset += PARAM_SIZE;
        }
        ++paramIndex;
    }

    public final void putAddress(final long value) {
        paramOffset = encoder.putAddress(buffer, paramOffset, value);
        ++paramIndex;
    }

    private final ObjectBuffer getObjectBuffer() {
        if (objectBuffer == null) {
            objectBuffer = new ObjectBuffer();
        }

        return objectBuffer;
    }

    public final void putArray(final byte[] array, int offset, int length, int flags) {
        paramOffset = encoder.putAddress(buffer, paramOffset, 0L);
        getObjectBuffer().putArray(paramIndex++, array, offset, length, flags);
    }

    public final void putArray(final short[] array, int offset, int length, int flags) {
        paramOffset = encoder.putAddress(buffer, paramOffset, 0L);
        getObjectBuffer().putArray(paramIndex++, array, offset, length, flags);
    }

    public final void putArray(final int[] array, int offset, int length, int flags) {
        paramOffset = encoder.putAddress(buffer, paramOffset, 0L);
        getObjectBuffer().putArray(paramIndex++, array, offset, length, flags);
    }

    public final void putArray(final long[] array, int offset, int length, int flags) {
        paramOffset = encoder.putAddress(buffer, paramOffset, 0L);
        getObjectBuffer().putArray(paramIndex++, array, offset, length, flags);
    }

    public final void putArray(final float[] array, int offset, int length, int flags) {
        paramOffset = encoder.putAddress(buffer, paramOffset, 0L);
        getObjectBuffer().putArray(paramIndex++, array, offset, length, flags);
    }

    public final void putArray(final double[] array, int offset, int length, int flags) {
        paramOffset = encoder.putAddress(buffer, paramOffset, 0L);
        getObjectBuffer().putArray(paramIndex++, array, offset, length, flags);
    }

    public final void putDirectBuffer(final java.nio.Buffer value, int offset, int length) {
        paramOffset = encoder.putAddress(buffer, paramOffset, 0L);
        getObjectBuffer().putDirectBuffer(paramIndex++, value, offset, length);
    }

    public final void putStruct(final byte[] struct, int offset) {
        final Type type = callContext.getParameterType(paramIndex);

        if (encoder.isRaw()) {
            paramOffset = FFI_ALIGN(paramOffset, type.alignment());
            System.arraycopy(struct, offset, buffer, paramOffset, type.size());
            paramOffset = FFI_ALIGN(paramOffset + type.size(), FFI_SIZEOF_ARG);
        } else {
            paramOffset = encoder.putAddress(buffer, paramOffset, 0L);
            getObjectBuffer().putArray(paramIndex, struct, offset, type.size(), ObjectBuffer.IN);
        }
        ++paramIndex;
    }

    public final void putStruct(final long struct) {
        final Type type = callContext.getParameterType(paramIndex);

        if (encoder.isRaw()) {
            paramOffset = FFI_ALIGN(paramOffset, type.alignment());
            MemoryIO.getInstance().getByteArray(struct, buffer, paramOffset, type.size());
            paramOffset = FFI_ALIGN(paramOffset + type.size(), FFI_SIZEOF_ARG);
        } else {
            paramOffset = encoder.putAddress(buffer, paramOffset, struct);
        }
        ++paramIndex;
    }

    public final void putObject(Object o, ObjectParameterStrategy strategy, ObjectParameterInfo info) {
        paramOffset = encoder.putAddress(buffer, paramOffset, 0L);
        getObjectBuffer().putObject(strategy.object(o), strategy.offset(o), strategy.length(o),
                ObjectBuffer.makeObjectFlags(info.ioflags(), strategy.typeInfo, paramIndex++));
    }

    public final void putObject(Object o, ObjectParameterStrategy strategy, int flags) {
        paramOffset = encoder.putAddress(buffer, paramOffset, 0L);
        getObjectBuffer().putObject(strategy.object(o), strategy.offset(o), strategy.length(o),
                ObjectBuffer.makeObjectFlags(flags, strategy.typeInfo, paramIndex++));
    }

    public final void putJNIEnvironment() {
        paramOffset = encoder.putAddress(buffer, paramOffset, 0L);
        getObjectBuffer().putJNI(paramIndex++, null, ObjectBuffer.JNIENV);
    }
    
    public final void putJNIObject(Object obj) {
        paramOffset = encoder.putAddress(buffer, paramOffset, 0L);
        getObjectBuffer().putJNI(paramIndex++, obj, ObjectBuffer.JNIOBJECT);
    }

    private static Encoder getEncoder() {
        Foreign foreign = Foreign.getInstance();
        Platform platform = Platform.getPlatform();

        if (platform.getCPU() == Platform.CPU.I386 && Foreign.getInstance().isRawParameterPackingEnabled()) {
            return newI386RawEncoder();
        }

        ArrayIO io = ByteOrder.nativeOrder().equals(ByteOrder.BIG_ENDIAN)
                ? platform.addressSize() == 64 ? getBE64IO() : getBE32IO()
                : platform.addressSize() == 64 ? getLE64IO() : getLE32IO();
        return foreign.isRawParameterPackingEnabled()
                ? newRawEncoder(io) : newDefaultEncoder(io);
    }

    static ArrayIO getBE32IO() {
        return BE32ArrayIO.INSTANCE;
    }

    static ArrayIO getLE32IO() {
        return LE32ArrayIO.INSTANCE;
    }

    static ArrayIO getLE64IO() {
        return LE64ArrayIO.INSTANCE;
    }

    static ArrayIO getBE64IO() {
        return BE64ArrayIO.INSTANCE;
    }

    private static Encoder newI386RawEncoder() {
        return new I386RawEncoder();
    }

    private static Encoder newRawEncoder(ArrayIO io) {
        return new RawEncoder(io);
    }

    private static Encoder newDefaultEncoder(ArrayIO io) {
        return new DefaultEncoder(io);
    }

    /**
     * Encodes java data types into native parameter frames
     */
    static abstract class Encoder {
        static final int BYTE_ALIGN = Type.SINT8.alignment();
        static final int BYTE_SIZE = Type.SINT8.size();
        static final int SHORT_ALIGN = Type.SSHORT.alignment();
        static final int SHORT_SIZE = Type.SSHORT.size();
        static final int INT_ALIGN = Type.SINT.alignment();
        static final int INT_SIZE = Type.SINT.size();
        static final int LONG_ALIGN = Type.SINT64.alignment();
        static final int LONG_SIZE = Type.SINT64.size();
        static final int FLOAT_ALIGN = Type.FLOAT.alignment();
        static final int FLOAT_SIZE = Type.FLOAT.size();
        static final int DOUBLE_ALIGN = Type.DOUBLE.alignment();
        static final int DOUBLE_SIZE = Type.DOUBLE.size();
        static final int LONGDOUBLE_ALIGN = Type.LONGDOUBLE.alignment();
        static final int LONGDOUBLE_SIZE = Type.LONGDOUBLE.size();
        static final int ADDRESS_ALIGN = Type.POINTER.alignment();
        static final int ADDRESS_SIZE = Type.POINTER.size();
        static final int FFI_SIZEOF_ARG = Platform.getPlatform().longSize() == 32 ? 4 : 8;

        /** Returns true if this <tt>Encoder</tt> is a raw encoder */
        public abstract boolean isRaw();

        /** Gets the size in bytes of the buffer required for the function */
        public abstract int getBufferSize(CallContext callContext);

        /**
         * Encodes a byte value into the byte array.
         *
         * @param buffer The destination byte buffer to place the encoded value.
         * @param offset The offset within the destination buffer to place the value.
         * @param value The value to encode.
         * @return The number of bytes consumed in encoding the value.
         */
        public abstract int putByte(byte[] buffer, int offset, int value);

        /**
         * Encodes a short value into the byte array.
         *
         * @param buffer The destination byte buffer to place the encoded value.
         * @param offset The offset within the destination buffer to place the value.
         * @param value The value to encode.
         * @return The number of bytes consumed in encoding the value.
         */
        public abstract int putShort(byte[] buffer, int offset, int value);

        /**
         * Encodes an int value into the byte array.
         *
         * @param buffer The destination byte buffer to place the encoded value.
         * @param offset The offset within the destination buffer to place the value.
         * @param value The value to encode.
         * @return The number of bytes consumed in encoding the value.
         */
        public abstract int putInt(byte[] buffer, int offset, int value);

        /**
         * Encodes a long value into the byte array.
         *
         * @param buffer The destination byte buffer to place the encoded value.
         * @param offset The offset within the destination buffer to place the value.
         * @param value The value to encode.
         * @return The number of bytes consumed in encoding the value.
         */
        public abstract int putLong(byte[] buffer, int offset, long value);

        /**
         * Encodes a float value into the byte array.
         *
         * @param buffer The destination byte buffer to place the encoded value.
         * @param offset The offset within the destination buffer to place the value.
         * @param value The value to encode.
         * @return The number of bytes consumed in encoding the value.
         */
        public abstract int putFloat(byte[] buffer, int offset, float value);

        /**
         * Encodes a double value into the byte array.
         *
         * @param buffer The destination byte buffer to place the encoded value.
         * @param offset The offset within the destination buffer to place the value.
         * @param value The value to encode.
         * @return The number of bytes consumed in encoding the value.
         */
        public abstract int putDouble(byte[] buffer, int offset, double value);

        /**
         * Encodes a native memory address value into the byte array.
         *
         * @param buffer The destination byte buffer to place the encoded value.
         * @param offset The offset within the destination buffer to place the value.
         * @param value The value to encode.
         * @return The number of bytes consumed in encoding the value.
         */
        public abstract int putAddress(byte[] buffer, int offset, long value);
    }

    /**
     * Packs arguments into a byte array in the format compliant with the
     * i386 sysv ABI, so the buffer can be copied directly onto the stack and
     * used.
     */
    private static final class I386RawEncoder extends Encoder {
        private static final ArrayIO IO = LE32ArrayIO.INSTANCE;

        public final boolean isRaw() {
            return true;
        }

        public final int getBufferSize(CallContext callContext) {
            return callContext.getRawParameterSize();
        }

        
        public final int putByte(byte[] buffer, int offset, int value) {
            IO.putByte(buffer, offset, value); return offset + 4;
        }
        public final int putShort(byte[] buffer, int offset, int value) {
            IO.putShort(buffer, offset, value); return offset + 4;
        }
        public final int putInt(byte[] buffer, int offset, int value) {
            IO.putInt(buffer, offset, value); return offset + 4;
        }
        public final int putLong(byte[] buffer, int offset, long value) {
            IO.putLong(buffer, offset, value); return offset + 8;
        }
        public final int putFloat(byte[] buffer, int offset, float value) {
            IO.putFloat(buffer, offset, value); return offset + 4;
        }
        public final int putDouble(byte[] buffer, int offset, double value) {
            IO.putDouble(buffer, offset, value); return offset + 8;
        }
        public final int putAddress(byte[] buffer, int offset, long value) {
            if (value != 0L) IO.putAddress(buffer, offset, value); return offset + 4;
        }
    }

    /**
     * Packs arguments into a byte array in the format compliant with the
     * i386 sysv ABI, so the buffer can be copied directly onto the stack and
     * used.
     */
    private static final class RawEncoder extends Encoder {
        private final ArrayIO io;

        private RawEncoder(ArrayIO io) {
            this.io = io;
        }

        public final boolean isRaw() {
            return true;
        }

        public final int getBufferSize(CallContext callContext) {
            return callContext.getRawParameterSize();
        }

        public final int putByte(byte[] buffer, int offset, int value) {
            offset = FFI_ALIGN(offset, FFI_SIZEOF_ARG);
            io.putByte(buffer, offset, value);
            return offset + BYTE_SIZE;
        }

        public final int putShort(byte[] buffer, int offset, int value) {
            offset = FFI_ALIGN(offset, FFI_SIZEOF_ARG);
            io.putShort(buffer, offset, value);
            return offset + SHORT_SIZE;
        }

        public final int putInt(byte[] buffer, int offset, int value) {
            offset = FFI_ALIGN(offset, FFI_SIZEOF_ARG);
            io.putInt(buffer, offset, value);
            return offset + INT_SIZE;
        }

        public final int putLong(byte[] buffer, int offset, long value) {
            offset = FFI_ALIGN(offset, FFI_SIZEOF_ARG);
            io.putLong(buffer, offset, value);
            return offset + LONG_SIZE;
        }

        public final int putFloat(byte[] buffer, int offset, float value) {
            offset = FFI_ALIGN(offset, FFI_SIZEOF_ARG);
            io.putFloat(buffer, offset, value);
            return offset + FLOAT_SIZE;
        }

        public final int putDouble(byte[] buffer, int offset, double value) {
            offset = FFI_ALIGN(offset, FFI_SIZEOF_ARG);
            io.putDouble(buffer, offset, value);
            return offset + DOUBLE_SIZE;
        }

        public final int putAddress(byte[] buffer, int offset, long value) {
            offset = FFI_ALIGN(offset, FFI_SIZEOF_ARG);
            if (value != 0L) io.putAddress(buffer, offset, value);
            return offset + ADDRESS_SIZE;
        }
    }

    private static final class DefaultEncoder extends Encoder {
        private final ArrayIO io;

        public DefaultEncoder(ArrayIO io) {
            this.io = io;
        }

        public final boolean isRaw() {
            return false;
        }
        
        public final int getBufferSize(CallContext callContext) {
            return callContext.getParameterCount() * PARAM_SIZE;
        }
        public final int putByte(byte[] buffer, int offset, int value) {
            io.putByte(buffer, offset, value); return offset + PARAM_SIZE;
        }
        public final int putShort(byte[] buffer, int offset, int value) {
            io.putShort(buffer, offset, value); return offset + PARAM_SIZE;
        }
        public final int putInt(byte[] buffer, int offset, int value) {
            io.putInt(buffer, offset, value); return offset + PARAM_SIZE;
        }
        public final int putLong(byte[] buffer, int offset, long value) {
            io.putLong(buffer, offset, value); return offset + PARAM_SIZE;
        }
        public final int putFloat(byte[] buffer, int offset, float value) {
            io.putFloat(buffer, offset, value); return offset + PARAM_SIZE;
        }
        public final int putDouble(byte[] buffer, int offset, double value) {
            io.putDouble(buffer, offset, value); return offset + PARAM_SIZE;
        }
        public final int putAddress(byte[] buffer, int offset, long value) {
            if (value != 0L) io.putAddress(buffer, offset, value); return offset + PARAM_SIZE;
        }
    }

    private static abstract class ArrayIO {
        public abstract void putByte(byte[] buffer, int offset, int value);
        public abstract void putShort(byte[] buffer, int offset, int value);
        public abstract void putInt(byte[] buffer, int offset, int value);
        public abstract void putLong(byte[] buffer, int offset, long value);
        public final void putFloat(byte[] buffer, int offset, float value) {
            putInt(buffer, offset, Float.floatToRawIntBits(value));
        }
        public final void putDouble(byte[] buffer, int offset, double value) {
            putLong(buffer, offset, Double.doubleToRawLongBits(value));
        }
        public abstract void putAddress(byte[] buffer, int offset, long value);
    }

    /**
     * Base class for all little-endian architecture array encoders.
     */
    private static abstract class LittleEndianArrayIO extends ArrayIO {
        public final void putByte(byte[] buffer, int offset, int value) {
            buffer[offset] = (byte) value;
        }
        public final void putShort(byte[] buffer, int offset, int value) {
            buffer[offset] = (byte) value;
            buffer[offset + 1] = (byte) (value >> 8);
        }
        public final void putInt(byte[] buffer, int offset, int value) {
            buffer[offset] = (byte) value;
            buffer[offset + 1] = (byte) (value >> 8);
            buffer[offset + 2] = (byte) (value >> 16);
            buffer[offset + 3] = (byte) (value >> 24);
        }
        public final void putLong(byte[] buffer, int offset, long value) {
            buffer[offset] = (byte) value;
            buffer[offset + 1] = (byte) (value >> 8);
            buffer[offset + 2] = (byte) (value >> 16);
            buffer[offset + 3] = (byte) (value >> 24);
            buffer[offset + 4] = (byte) (value >> 32);
            buffer[offset + 5] = (byte) (value >> 40);
            buffer[offset + 6] = (byte) (value >> 48);
            buffer[offset + 7] = (byte) (value >> 56);
        }
    }

    /**
     * Little endian, 32 bit implementation of <tt>ArrayIO</tt>
     */
    private static final class LE32ArrayIO extends LittleEndianArrayIO {
        static final ArrayIO INSTANCE = new LE32ArrayIO();
        public final void putAddress(byte[] buffer, int offset, long value) {
            buffer[offset] = (byte) value;
            buffer[offset + 1] = (byte) (value >> 8);
            buffer[offset + 2] = (byte) (value >> 16);
            buffer[offset + 3] = (byte) (value >> 24);
        }
    }

    /**
     * Little endian, 64 bit implementation of <tt>ArrayIO</tt>
     */
    private static final class LE64ArrayIO extends LittleEndianArrayIO {
        static final ArrayIO INSTANCE = new LE64ArrayIO();
        public final void putAddress(byte[] buffer, int offset, long value) {
            putLong(buffer, offset, value);
        }
    }

    /**
     * Base class for all big-endian architecture array encoders.
     */
    private static abstract class BigEndianArrayIO extends ArrayIO {

        public final void putByte(byte[] buffer, int offset, int value) {
            buffer[offset] = (byte) value;
        }

        public final void putShort(byte[] buffer, int offset, int value) {
            buffer[offset + 0] = (byte) (value >> 8);
            buffer[offset + 1] = (byte) value;
            
        }

        public final void putInt(byte[] buffer, int offset, int value) {
            buffer[offset + 0] = (byte) (value >> 24);
            buffer[offset + 1] = (byte) (value >> 16);
            buffer[offset + 2] = (byte) (value >> 8);
            buffer[offset + 3] = (byte) value;
        }

        public final void putLong(byte[] buffer, int offset, long value) {
            buffer[offset + 0] = (byte) (value >> 56);
            buffer[offset + 1] = (byte) (value >> 48);
            buffer[offset + 2] = (byte) (value >> 40);
            buffer[offset + 3] = (byte) (value >> 32);
            buffer[offset + 4] = (byte) (value >> 24);
            buffer[offset + 5] = (byte) (value >> 16);
            buffer[offset + 6] = (byte) (value >> 8);
            buffer[offset + 7] = (byte) value;
        }
    }

    /**
     * Big endian, 32 bit array encoder
     */
    private static final class BE32ArrayIO extends BigEndianArrayIO {
        static final ArrayIO INSTANCE = new BE32ArrayIO();
        public void putAddress(byte[] buffer, int offset, long value) {
            buffer[offset + 0] = (byte) (value >> 24);
            buffer[offset + 1] = (byte) (value >> 16);
            buffer[offset + 2] = (byte) (value >> 8);
            buffer[offset + 3] = (byte) value;
        }

    }

    /**
     * Big endian, 64 bit array encoder
     */
    private static final class BE64ArrayIO extends BigEndianArrayIO {
        static final ArrayIO INSTANCE = new BE64ArrayIO();
        public void putAddress(byte[] buffer, int offset, long value) {
            putLong(buffer, offset, value);
        }
    }

    /**
     * Aligns an address to a boundary
     *
     * @param v The address to roundup
     * @param a The boundary to align to.
     * @return The aligned address.
     */
    static int FFI_ALIGN(int v, int a) {
        return ((v - 1) | (a - 1)) + 1;
    }

}

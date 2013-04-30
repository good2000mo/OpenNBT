package ch.spacebase.opennbt.stream;

import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;

import ch.spacebase.opennbt.NBTConstants;
import ch.spacebase.opennbt.NBTUtils;
import ch.spacebase.opennbt.tag.ByteArrayTag;
import ch.spacebase.opennbt.tag.ByteTag;
import ch.spacebase.opennbt.tag.CompoundTag;
import ch.spacebase.opennbt.tag.DoubleTag;
import ch.spacebase.opennbt.tag.EndTag;
import ch.spacebase.opennbt.tag.FloatTag;
import ch.spacebase.opennbt.tag.IntArrayTag;
import ch.spacebase.opennbt.tag.IntTag;
import ch.spacebase.opennbt.tag.ListTag;
import ch.spacebase.opennbt.tag.LongTag;
import ch.spacebase.opennbt.tag.ShortTag;
import ch.spacebase.opennbt.tag.StringTag;
import ch.spacebase.opennbt.tag.Tag;
import ch.spacebase.opennbt.tag.custom.DoubleArrayTag;
import ch.spacebase.opennbt.tag.custom.FloatArrayTag;
import ch.spacebase.opennbt.tag.custom.LongArrayTag;
import ch.spacebase.opennbt.tag.custom.ObjectArrayTag;
import ch.spacebase.opennbt.tag.custom.ObjectTag;
import ch.spacebase.opennbt.tag.custom.ShortArrayTag;
import ch.spacebase.opennbt.tag.custom.StringArrayTag;

/*
 * OpenNBT License
 * 
 * JNBT Copyright (c) 2010 Graham Edgecombe
 * OpenNBT Copyright(c) 2012 Steveice10
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *       
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *       
 *     * Neither the name of the OpenNBT team nor the names of its
 *       contributors may be used to endorse or promote products derived from
 *       this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE. 
 */

/**
 * <p>This class writes <strong>NBT</strong>, or
 * <strong>Named Binary Tag</strong> <code>Tag</code> objects to an underlying
 * <code>OutputStream</code>.</p>
 * 
 * <p>The NBT format was created by Markus Persson, and the specification may
 * be found at <a href="http://www.minecraft.net/docs/NBT.txt">
 * http://www.minecraft.net/docs/NBT.txt</a>.</p>
 */
public final class NBTOutputStream implements Closeable {
	
	private static final Logger logger = Logger.getLogger("NBTOutputStream");
	
	/**
	 * The output stream.
	 */
	private final DataOutputStream os;
	
	/**
	 * Creates a new <code>NBTOutputStream</code>, which will write data to the
	 * specified underlying output stream.
	 * @param os The output stream.
	 * @throws IOException if an I/O error occurs.
	 */
	public NBTOutputStream(OutputStream os) throws IOException {
		this.os = new DataOutputStream(new GZIPOutputStream(os));
	}
	
	/**
	 * Writes a tag.
	 * @param tag The tag to write.
	 * @throws IOException if an I/O error occurs.
	 */
	public void writeTag(Tag tag) throws IOException { 
		int type = NBTUtils.getTypeCode(tag.getClass());
		if(type == NBTConstants.TYPE_UNKNOWN) {
			logger.warning("Unknown tag found while writing, ignoring...");
		}

		String name = tag.getName();
		byte[] nameBytes = name.getBytes(NBTConstants.CHARSET);
		
		os.writeByte(type);
		os.writeShort(nameBytes.length);
		os.write(nameBytes);
		
		if(type == NBTConstants.TYPE_END) {
			throw new IOException("Named TAG_End not permitted.");
		}
		
		writeTagPayload(tag);
	}

	/**
	 * Writes tag payload.
	 * @param tag The tag.
	 * @throws IOException if an I/O error occurs.
	 */
	private void writeTagPayload(Tag tag) throws IOException {
		int type = NBTUtils.getTypeCode(tag.getClass());
		switch(type) {
		case NBTConstants.TYPE_END:
			writeEndTagPayload((EndTag) tag);
			break;
		case NBTConstants.TYPE_BYTE:
			writeByteTagPayload((ByteTag) tag);
			break;
		case NBTConstants.TYPE_SHORT:
			writeShortTagPayload((ShortTag) tag);
			break;
		case NBTConstants.TYPE_INT:
			writeIntTagPayload((IntTag) tag);
			break;
		case NBTConstants.TYPE_LONG:
			writeLongTagPayload((LongTag) tag);
			break;
		case NBTConstants.TYPE_FLOAT:
			writeFloatTagPayload((FloatTag) tag);
			break;
		case NBTConstants.TYPE_DOUBLE:
			writeDoubleTagPayload((DoubleTag) tag);
			break;
		case NBTConstants.TYPE_BYTE_ARRAY:
			writeByteArrayTagPayload((ByteArrayTag) tag);
			break;
		case NBTConstants.TYPE_STRING:
			writeStringTagPayload((StringTag) tag);
			break;
		case NBTConstants.TYPE_LIST:
			writeListTagPayload((ListTag<?>) tag);
			break;
		case NBTConstants.TYPE_COMPOUND:
			writeCompoundTagPayload((CompoundTag) tag);
			break;
		case NBTConstants.TYPE_INT_ARRAY:
			writeIntArrayTagPayload((IntArrayTag) tag);
			break;
		case NBTConstants.TYPE_DOUBLE_ARRAY:
			writeDoubleArrayTagPayload((DoubleArrayTag) tag);
			break;
		case NBTConstants.TYPE_FLOAT_ARRAY:
			writeFloatArrayTagPayload((FloatArrayTag) tag);
			break;
		case NBTConstants.TYPE_LONG_ARRAY:
			writeLongArrayTagPayload((LongArrayTag) tag);
			break;
		case NBTConstants.TYPE_OBJECT_ARRAY:
			writeObjectArrayTagPayload((ObjectArrayTag) tag);
			break;
		case NBTConstants.TYPE_OBJECT:
			writeObjectTagPayload((ObjectTag) tag);
			break;
		case NBTConstants.TYPE_SHORT_ARRAY:
			writeShortArrayTagPayload((ShortArrayTag) tag);
			break;
		case NBTConstants.TYPE_STRING_ARRAY:
			writeStringArrayTagPayload((StringArrayTag) tag);
			break;
		default:
			logger.warning("Unknown tag found while writing, ignoring...");
		}
	}

	/**
	 * Writes a <code>TAG_Byte</code> tag.
	 * @param tag The tag.
	 * @throws IOException if an I/O error occurs.
	 */
	private void writeByteTagPayload(ByteTag tag) throws IOException {
		os.writeByte(tag.getValue());
	}

	/**
	 * Writes a <code>TAG_Byte_Array</code> tag.
	 * @param tag The tag.
	 * @throws IOException if an I/O error occurs.
	 */
	private void writeByteArrayTagPayload(ByteArrayTag tag) throws IOException {
		byte[] bytes = tag.getValue();
		os.writeInt(bytes.length);
		os.write(bytes);
	}

	/**
	 * Writes a <code>TAG_Compound</code> tag.
	 * @param tag The tag.
	 * @throws IOException if an I/O error occurs.
	 */
	private void writeCompoundTagPayload(CompoundTag tag) throws IOException {
		for(Tag childTag : tag.values()) {
			writeTag(childTag);
		}
		os.writeByte((byte) 0); // end tag - better way?
	}

	/**
	 * Writes a <code>TAG_List</code> tag.
	 * @param tag The tag.
	 * @throws IOException if an I/O error occurs.
	 */
	private void writeListTagPayload(ListTag<?> tag) throws IOException {
		Class<? extends Tag> clazz = tag.getType();
		int size = tag.size();
		
		os.writeByte(NBTUtils.getTypeCode(clazz));
		os.writeInt(size);
		for(Tag t : tag) {
			this.writeTagPayload(t);
		}
	}

	/**
	 * Writes a <code>TAG_String</code> tag.
	 * @param tag The tag.
	 * @throws IOException if an I/O error occurs.
	 */
	private void writeStringTagPayload(StringTag tag) throws IOException {
		byte[] bytes = tag.getValue() != null ? tag.getValue().getBytes(NBTConstants.CHARSET) : new byte[0];
		os.writeShort(bytes.length);
		os.write(bytes);
	}

	/**
	 * Writes a <code>TAG_Double</code> tag.
	 * @param tag The tag.
	 * @throws IOException if an I/O error occurs.
	 */
	private void writeDoubleTagPayload(DoubleTag tag) throws IOException {
		os.writeDouble(tag.getValue());
	}

	/**
	 * Writes a <code>TAG_Float</code> tag.
	 * @param tag The tag.
	 * @throws IOException if an I/O error occurs.
	 */
	private void writeFloatTagPayload(FloatTag tag) throws IOException {
		os.writeFloat(tag.getValue());
	}

	/**
	 * Writes a <code>TAG_Long</code> tag.
	 * @param tag The tag.
	 * @throws IOException if an I/O error occurs.
	 */
	private void writeLongTagPayload(LongTag tag) throws IOException {
		os.writeLong(tag.getValue());
	}

	/**
	 * Writes a <code>TAG_Int</code> tag.
	 * @param tag The tag.
	 * @throws IOException if an I/O error occurs.
	 */
	private void writeIntTagPayload(IntTag tag) throws IOException {
		os.writeInt(tag.getValue());
	}

	/**
	 * Writes a <code>TAG_Short</code> tag.
	 * @param tag The tag.
	 * @throws IOException if an I/O error occurs.
	 */
	private void writeShortTagPayload(ShortTag tag) throws IOException {
		os.writeShort(tag.getValue());
	}

	/**
	 * Writes a <code>TAG_Empty</code> tag.
	 * @param tag The tag.
	 * @throws IOException if an I/O error occurs.
	 */
	private void writeEndTagPayload(EndTag tag) {
		/* empty */
	}
	
	/** Writes a <code>TAG_Int_Array<code> tag.
	 * @param tag The tag
	 * @throws IOException if an I/O error occurs.
	 */
    private void writeIntArrayTagPayload(IntArrayTag tag) throws IOException {
        int[] data = tag.getValue();
        
        os.writeInt(data.length);
        
        for (int i = 0; i < data.length; i++) {
            os.writeInt(data[i]);
        } 
    }
    
	/** Writes a <code>TAG_Double_Array<code> tag.
	 * @param tag The tag
	 * @throws IOException if an I/O error occurs.
	 */
    private void writeDoubleArrayTagPayload(DoubleArrayTag tag) throws IOException {
        double[] data = tag.getValue();
        
        os.writeInt(data.length);
        
        for (int i = 0; i < data.length; i++) {
            os.writeDouble(data[i]);
        } 
    }
    
	/** Writes a <code>TAG_Float_Array<code> tag.
	 * @param tag The tag
	 * @throws IOException if an I/O error occurs.
	 */
    private void writeFloatArrayTagPayload(FloatArrayTag tag) throws IOException {
        float[] data = tag.getValue();
        
        os.writeInt(data.length);
        
        for (int i = 0; i < data.length; i++) {
            os.writeFloat(data[i]);
        } 
    }
    
	/** Writes a <code>TAG_Long_Array<code> tag.
	 * @param tag The tag
	 * @throws IOException if an I/O error occurs.
	 */
    private void writeLongArrayTagPayload(LongArrayTag tag) throws IOException {
        long[] data = tag.getValue();
        
        os.writeInt(data.length);
        
        for (int i = 0; i < data.length; i++) {
            os.writeLong(data[i]);
        } 
    }
    
	/** Writes a <code>TAG_Object_Array<code> tag.
	 * @param tag The tag
	 * @throws IOException if an I/O error occurs.
	 */
    private void writeObjectArrayTagPayload(ObjectArrayTag tag) throws IOException {
        Object[] data = tag.getValue();
        
        os.writeInt(data.length);
        ObjectOutputStream str = new ObjectOutputStream(os);
        
        for (int i = 0; i < data.length; i++) {
            str.writeObject(data[i]);
        } 
    }
    
	/** Writes a <code>TAG_Object<code> tag.
	 * @param tag The tag
	 * @throws IOException if an I/O error occurs.
	 */
    private void writeObjectTagPayload(ObjectTag tag) throws IOException {
    	(new ObjectOutputStream(os)).writeObject(tag.getValue());
    }
    
	/** Writes a <code>TAG_Short_Array<code> tag.
	 * @param tag The tag
	 * @throws IOException if an I/O error occurs.
	 */
    private void writeShortArrayTagPayload(ShortArrayTag tag) throws IOException {
        short[] data = tag.getValue();
        
        os.writeInt(data.length);
        
        for (int i = 0; i < data.length; i++) {
            os.writeShort(data[i]);
        } 
    }
    
	/** Writes a <code>TAG_String_Array<code> tag.
	 * @param tag The tag
	 * @throws IOException if an I/O error occurs.
	 */
    private void writeStringArrayTagPayload(StringArrayTag tag) throws IOException {
        String[] data = tag.getValue();
        
        os.writeInt(data.length);
        byte[] bytes;
        
        for (int i = 0; i < data.length; i++) {
    		bytes = data[i].getBytes(NBTConstants.CHARSET);
    		os.writeShort(bytes.length);
    		os.write(bytes);
        } 
    }

	@Override
	public void close() throws IOException {
		os.close();
	}

}

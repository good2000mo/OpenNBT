package ch.spacebase.opennbt.tag.custom;

import java.util.Arrays;

import ch.spacebase.opennbt.tag.Tag;

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
 *     * Neither the name of the JNBT team nor the names of its
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
 * The <code>TAG_Long_Array</code> tag.
 */
public final class LongArrayTag extends Tag {

	/**
	 * The value.
	 */
	private final long[] value;

	/**
	 * Creates the tag.
	 * 
	 * @param name
	 *            The name.
	 * @param value
	 *            The value.
	 */
	public LongArrayTag(String name, long[] value) {
		super(name);
		this.value = value;
	}

	@Override
	public long[] getValue() {
		return value;
	}

	@Override
	public String toString() {
		String name = getName();
		String append = "";

		if (name != null && !name.equals("")) {
			append = "(\"" + this.getName() + "\")";
		}

		return "TAG_Long_Array" + append + ": " + Arrays.toString(value);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof LongArrayTag)) return false;
		
		LongArrayTag tag = (LongArrayTag) obj;
		
		return Arrays.equals(this.getValue(), tag.getValue()) && this.getName().equals(tag.getName());
	}

	@Override
	public LongArrayTag clone() {
		long[] clonedArray = this.getValue().clone();

		return new LongArrayTag(this.getName(), clonedArray);
	}

}
package cs321.btree;

import java.nio.ByteBuffer;

/**
* Represents an object stored in the B-Tree, containing a value and its frequency.
*/
public class TreeObject implements Comparable<TreeObject> {

   private long value;
   private long frequency;

   /**
    * Constructs a TreeObject with the given value and frequency.
    *
    * @param value     The value to be stored in the TreeObject.
    * @param frequency The frequency associated with the value.
    */
   public TreeObject(long value, long frequency) {
       super();
       this.value = value;
       this.frequency = frequency;
   }

   /**
    * Constructs a TreeObject with the given value and a default frequency of 1.
    *
    * @param value The value to be stored in the TreeObject.
    */
   public TreeObject(long value) {
       this(value, 1);
   }

   /**
    * Returns the value stored in the TreeObject.
    *
    * @return The value stored in the TreeObject.
    */
   public long getValue() { return value; }

   /**
    * Returns the frequency associated with the value.
    *
    * @return The frequency associated with the value.
    */
   public long getFrequency() { return frequency; }

   /**
    * Writes the value and frequency of the TreeObject to the given ByteBuffer.
    *
    * @param buffer The ByteBuffer to write the data to.
    */
   public void writeToBuffer(ByteBuffer buffer) {
       buffer.putLong(value);
       buffer.putLong(frequency);
   }

   /**
    * Reads the value and frequency of the TreeObject from the given ByteBuffer.
    *
    * @param buffer The ByteBuffer to read the data from.
    */
   public void readFromBuffer(ByteBuffer buffer) {
       this.value = buffer.getLong();
       this.frequency = buffer.getLong();
   }

   @Override
   public int compareTo(TreeObject other) {
       if (this.value < other.value)
           return -1;
       else if (this.value == other.value)
           return 0;
       else // value > other.value
           return +1;
   }

   /**
    * Returns a string representation of the TreeObject in the format "value frequency".
    *
    * @return A string representation of the TreeObject.
    */
   public String toString() {
       return value + " " + frequency;
   }

   public Object getCount() {
       return (int) this.frequency;
   }

   public void setFrequency(long frequency) {
       this.frequency = frequency;
   }

   public void setValue(long value) {
       this.value = value;
   }

   public void incrementFrequency() {
       this.frequency++;
   }
}
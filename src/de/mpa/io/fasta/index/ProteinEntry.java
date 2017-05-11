package de.mpa.io.fasta.index;

import org.apache.commons.lang3.builder.EqualsBuilder;

public class ProteinEntry implements Comparable<ProteinEntry> {
	
	private String header;
	private String sequence;
	
	public ProteinEntry(String header, String sequence) {
		this.header = header;
		this.sequence = sequence;
	}
	public String getHeader() {
		return header;
	}
	public String getSequence() {
		return sequence;
	}
	
	@Override
    public boolean equals(Object obj) {
       if (!(obj instanceof ProteinEntry))
            return false;
        if (obj == this)
            return true;

        ProteinEntry entry = (ProteinEntry) obj;
        return new EqualsBuilder().
            append(header, entry.header).
            append(sequence, entry.sequence).
            isEquals();
    }

	@Override
	public int compareTo(ProteinEntry entry) {
		int value = this.header.compareTo(entry.header);
        return value == 0 ? this.sequence.compareTo(entry.sequence) : value;
	}
}

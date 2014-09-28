package edu.buffalo.cse.irf14.index;
 
import java.util.List;
import java.util.Vector;

public class TopListings {

 
	protected List<TermIndexDictionaryElement> m_termSortedCount;
	
	public TopListings() {
 
		m_termSortedCount = new Vector<TermIndexDictionaryElement>();
	}
	
	public TermIndexDictionaryElement get(int index)
	{
		return m_termSortedCount.get(index);
	}

	public void insertSorted(TermIndexDictionaryElement value) {
		int min = 0;
		int max = m_termSortedCount.size() - 1;
		 if(m_termSortedCount.size() == 0)
		 {
			 m_termSortedCount.add(value);
			 return;
		 }
		 else if(m_termSortedCount.get(max).getoccurrences() >= value.getoccurrences() )
		 {
			 m_termSortedCount.add(value);
			 return;
		 }
		 else if(m_termSortedCount.get(min).getoccurrences() <= value.getoccurrences())
		 {
			 m_termSortedCount.add(min, value);
			 return;
		 }
		 
		 insertSort(value, min, max);
		 
	}
	
	protected void insertSort(TermIndexDictionaryElement value, int min, int max)
	{
		assert(min <= max);
		int mid = (min + max) /2;
		
		if(mid == min)
		{
			m_termSortedCount.add(mid, value);
		}
		if(m_termSortedCount.get(mid).getoccurrences() > value.getoccurrences())
		{
			
			insertSort(value, mid + 1, max);
		}
		else if(m_termSortedCount.get(mid).getoccurrences() < value.getoccurrences())
		{
			insertSort(value, min, mid);
		}
		else
		{
			m_termSortedCount.add(mid, value);
		}
	}

	/**
	 * Clear contents
	 */
	public void clear() {
		m_termSortedCount.clear();
	}
}

package edu.rutgers.MOST.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This is a model reducer class used for
 * compressing FBA and GDBB models
 * @author Tony
 */
public class ModelCompressor
{
	private ArrayList< Map< Integer, Double > > sMatrix = null;
	private ArrayList< Map< Integer, Double > > gMatrix = null;
	private ArrayList< Map< Integer, Double > > recMat = null;
	private Map< Integer, Double > objVec = null;
	private ArrayList< Double > lowerBounds = null;
	private ArrayList< Double > upperBounds = null;
	
	private int rowCount()
	{
		return sMatrix.size();
	}
	
	private int columnCount()
	{
		return lowerBounds.size();
	}
	
	private double getsMat( int i, int j )
	{
		Double res = sMatrix.get( i ).get( j );
		return res == null ? 0.0 : res;
	}
	
	private void setsMat( int i, int j, double val )
	{
		sMatrix.get( i ).put( j, val );
	}
	
	private double getgMat( int i, int j )
	{
		Double res = gMatrix.get( i ).get( j );
		return res == null ? new Double( 0.0 ) : res;
	}
	
	private void setgMat( int i, int j, double val )
	{
		gMatrix.get( i ).put( j, val );
	}
	
	private void createRecMat()
	{
		recMat = new ArrayList< Map< Integer, Double > >();
		int j = 0;
		for( int i = 0; i < columnCount(); ++i )
		{
			HashMap< Integer, Double > map = new HashMap< Integer, Double >();
			map.put( j++, 1.0 );
			recMat.add( map );
		}
	}

	public double getrMat( int i, int j )
	{
		Double res = recMat.get( i ).get( j );
		return res == null ? 0.0 : res;
	}
	
	public void setrMat( int i, int j, double val )
	{
		recMat.get( i ).put( j, val );
	}

	public Map< Integer, Double > getObjVec()
	{
		return objVec;
	}

	public void setObjVec( Map< Integer, Double > objVec )
	{
		this.objVec = objVec;
	}

	public ArrayList< Double > getLowerBounds()
	{
		return this.lowerBounds;
	}
	public ArrayList< Double > getUpperBounds()
	{
		return this.upperBounds;
	}
	
	public ModelCompressor()
	{
	}

	public ArrayList< Map< Integer, Double > > getsMatrix()
	{
		return sMatrix;
	}

	public void setsMatrix( ArrayList< Map< Integer, Double > > sMatrix )
	{
		this.sMatrix = sMatrix;
	}

	public ArrayList< Map< Integer, Double > > getgMatrix()
	{
		return gMatrix;
	}

	public void setgMatrix( ArrayList< Map< Integer, Double > > gMatrix )
	{
		this.gMatrix = gMatrix;
	}
	
	public void compressNet()
	{
		if( sMatrix == null || /*gMatrix == null ||*/
				objVec == null || lowerBounds == null || upperBounds == null )
			return;

		// create the recmap
		createRecMat();

		// start the compression
		int orColCount;
		int orRowCount;
		do
		{
			orColCount = columnCount();
			orRowCount = rowCount();
			// keep only the columns that have a nonzero value
			// (Y-dimension) across the matrix
			for( int j = 0; j < columnCount(); ++j )
			{
				boolean isZeroColumn = true;
				for( int i = 0; i < rowCount(); ++i )
					if( getsMat( i, j ) != 0.0 )
						isZeroColumn = false;
				if( isZeroColumn )
					this.removeColumn( j );
			}
			
			// remove the rows (reactions) that have only 1 nonzero column (flux)
			// due to steady-state constraint, it will optimize to be 0 anyway
			for( int i = 0; i < rowCount(); ++i )
			{
				ArrayList< Integer > cols = new ArrayList< Integer >();
				for( int j = 0; j < columnCount(); ++j )
					if( getsMat( i, j ) != 0.0 )
						cols.add( j );
				if( cols.size() == 1 )
				{
					removeColumn( cols.get( 0 ) );
					removeRow( i );
				}
			}
		
			
			
			for( boolean repeat = true; repeat; )
			{
				// find the rows that have only 2 nonzero columns (fluxes)
				// and merge them
				ArrayList< Integer > mergecols = new ArrayList< Integer >();
				ArrayList< Double > mergecoefs = new ArrayList< Double >();
				int candmass = 0;
	
				for( int i = 0; i < rowCount(); ++i )
				{
					candmass = i;
					for( int j = 0; j < columnCount(); ++j )
					{
						if( getsMat( i, j ) != 0 )
						{
							mergecols.add( j );
							mergecoefs.add( getsMat( i, j ) );
						}
					}
					if( mergecols.size() == 2 )
						break;
					else
					{
						mergecols.clear();
						mergecoefs.clear();
					}
				}
				
				if( mergecols.size() != 2 )
				{
					repeat = false;
					continue;
				}
				
				// for sMatrix
				for( int i = 0; i < rowCount(); ++i )
				{
					double val0 = getsMat( i, mergecols.get( 0 ) ); // current row
					double val1 = getsMat( i, mergecols.get( 1 ) ); // current row
					
					// for sMatrix
					setsMat( i, mergecols.get( 0 ), val0 - ( val1 / mergecoefs.get( 1 ) * mergecoefs.get( 0 ) ) );
				}
				
				// for recMat
				for( int i = 0; i < recMat.size(); ++i )
				{
					double val0_rec = getrMat( i, mergecols.get( 0 ) ); // current row reccoef
					double val1_rec = getrMat( i, mergecols.get( 1 ) ); // current row reccoef
					
					setrMat( i, mergecols.get( 0 ), val0_rec - ( val1_rec / mergecoefs.get( 1 ) * mergecoefs.get( 0 ) ) );
				}
				
				// shift the objective
				Double obj_v0 = objVec.get( mergecols.get( 0 ) );
				obj_v0 = obj_v0 == null ? 0.0 : obj_v0;
				Double obj_v1 = objVec.get( mergecols.get( 1 ) );
				obj_v1 = obj_v1 == null ? 0.0 : obj_v1;
				objVec.put( mergecols.get( 0 ), obj_v0 - obj_v1 / mergecoefs.get( 1 ) * mergecoefs.get( 0 ) );
				
				// shift the upper/lower bounds
				if( mergecoefs.get( 0 ) / mergecoefs.get( 1 ) > 0 )
				{
					lowerBounds.set( mergecols.get( 0 ), Math.max( lowerBounds.get( mergecols.get( 0 ) ), -upperBounds.get( mergecols.get( 1 ) ) * mergecoefs.get( 1 ) / mergecoefs.get( 0 ) ) );
					upperBounds.set( mergecols.get( 0 ), Math.min( upperBounds.get( mergecols.get( 0 ) ), -lowerBounds.get( mergecols.get( 1 ) ) * mergecoefs.get( 1 ) / mergecoefs.get( 0 ) ) );
				}
				else
				{
					lowerBounds.set( mergecols.get( 0 ), Math.max( lowerBounds.get( mergecols.get( 0 ) ), -lowerBounds.get( mergecols.get( 1 ) ) * mergecoefs.get( 1 ) / mergecoefs.get( 0 ) ) );
					upperBounds.set( mergecols.get( 0 ), Math.min( upperBounds.get( mergecols.get( 0 ) ), -upperBounds.get( mergecols.get( 1 ) ) * mergecoefs.get( 1 ) / mergecoefs.get( 0 ) ) );
				}

				removeColumn( mergecols.get( 1 ) );
				if( lowerBounds.get( mergecols.get( 0 ) ) >= upperBounds.get( mergecols.get( 0 ) ) )
					removeColumn( mergecols.get( 0 ) );
				removeRow( candmass );
			}
			
			
			
		} while( rowCount() < orRowCount || columnCount() < orColCount );
		
	}

	public ArrayList< Double > decompress( ArrayList< Double > v )
	{
		ArrayList< Double > result = new ArrayList< Double >();
		for( int i = 0; i < recMat.size(); ++i )
		{
			double dot = 0.0;
			for( int j = 0; j < v.size(); ++j )
			{
				dot += getrMat(i,j) * v.get( j );
			}
			result.add( dot );
		}
		
		return result;
	}
	
	private void removeColumn( int j )
	{
	/*	for( Map< Integer, Double > con : sMatrix )
			for( Entry< Integer, Double > term : con.entrySet() )
				if( term.getKey().equals( j ) )
					term.setValue( 0.0 );
	*/
	
		// sMat
	 	for( int i = 0; i < rowCount(); ++i )
	 	{
	 		Map< Integer, Double > oldRow = sMatrix.get( i );
	 		Map< Integer, Double > newRow = new HashMap< Integer, Double >();
	 		for( Entry< Integer, Double > entry : oldRow.entrySet() )
	 			if( !entry.getValue().equals( 0.0 ) )
		 			if( entry.getKey() < j )
		 				newRow.put( entry.getKey(), entry.getValue() );
		 			else if( entry.getKey() > j )
		 				newRow.put( entry.getKey() - 1, entry.getValue() );
	 		sMatrix.set( i, newRow );
	 	}
	 	
	 	// recMat
	 	for( int i = 0; i < recMat.size(); ++i )
	 	{
	 		Map< Integer, Double > oldRow = recMat.get( i );
	 		Map< Integer, Double > newRow = new HashMap< Integer, Double >();
	 		for( Entry< Integer, Double > entry : oldRow.entrySet() )
	 			if( !entry.getValue().equals( 0.0 ) )
		 			if( entry.getKey() < j )
		 				newRow.put( entry.getKey(), entry.getValue() );
		 			else if( entry.getKey() > j )
		 				newRow.put( entry.getKey() - 1, entry.getValue() );
	 		recMat.set( i, newRow );
	 	}
	 	
	 	//objVec
 		Map< Integer, Double > newObjVec = new HashMap< Integer, Double >();
 		for( Entry< Integer, Double > entry : objVec.entrySet() )
 			if( entry.getKey() < j )
 				newObjVec.put( entry.getKey(), entry.getValue() );
 			else if( entry.getKey() > j )
 				newObjVec.put( entry.getKey() - 1, entry.getValue() );
 		
	 	objVec = newObjVec;
	 	lowerBounds.remove( j );
	 	upperBounds.remove( j );
	
	}
	
	private void removeRow( int i )
	{
		// sMatrix.get( i ).clear();
		
		sMatrix.remove( i );
	}

	public void setLowerBounds( ArrayList< Double > lowerBounds )
	{
		this.lowerBounds = lowerBounds;
	}

	public void setUpperBounds( ArrayList< Double > upperBounds )
	{
		this.upperBounds = upperBounds;
	}
}

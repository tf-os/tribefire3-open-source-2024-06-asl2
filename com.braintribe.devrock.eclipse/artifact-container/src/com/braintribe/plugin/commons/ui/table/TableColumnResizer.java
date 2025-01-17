// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
// 
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
//     http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// ============================================================================
package com.braintribe.plugin.commons.ui.table;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class TableColumnResizer extends ControlAdapter {
	
	private List<TableColumn>  columns = null;
	private int [] columnWeights = null;
	private Composite parent = null;
	int sumOfColumnWeights = 0;
	private Table table = null;
	
	public void setColumns(List<TableColumn> columns) {
		this.columns = columns;
	}
	public void setColumnWeights(int[] columnWeights) {
		this.columnWeights = columnWeights;
		for (int i = 0; i < columnWeights.length; i++) {
			sumOfColumnWeights += columnWeights[i];
		}
	}
	public void setParent(Composite parent) {
		this.parent = parent;
	}
	
	public void setTable(Table table) {
		this.table = table;
	}

	private void recalculateColumns(int width) { 				 
		 float widthPerColumn = ((float)width) / sumOfColumnWeights;
    	 for (int i = 0; i < columns.size(); i++) {
    		TableColumn column = columns.get(i);
    		 column.setWidth( (int) (widthPerColumn * columnWeights[i]));
    	 }
	 }
	 
	@Override
    public void controlResized(ControlEvent e) {
      Rectangle area = parent.getClientArea();
      Point preferredSize = table.computeSize(SWT.DEFAULT, SWT.DEFAULT);
      int width = area.width - 2*table.getBorderWidth();
      if (preferredSize.y > area.height + table.getHeaderHeight()) {
        // Subtract the scrollbar width from the total column width
        // if a vertical scrollbar will be required
        Point vBarSize = table.getVerticalBar().getSize();
        width -= vBarSize.x;
      }
      recalculateColumns( width);
  	  table.setSize(area.width, area.height);
  	/*
      Point oldSize = table.getSize();      
      if (oldSize.x > area.width) {
        // table is getting smaller so make the columns 
        // smaller first and then resize the table to
        // match the client area width
    	recalculateColumns( width);
    	table.setSize(area.width, area.height);
      } else {
        // table is getting bigger so make the table 
        // bigger first and then make the columns wider
        // to match the client area width 		    	
    	 recalculateColumns(width);
    	 table.setSize(area.width, area.height);      
      }
      */
    }
  
}

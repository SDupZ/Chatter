/* Author: Simon du Preez 
* Date: 2013.01.13
*
* Version History:
*  1.00 - Created Prsogram
*/ 

import javax.swing.tree.DefaultMutableTreeNode;

public class VCTreeNode extends DefaultMutableTreeNode{
	private Contact contact;
	private String nodeName;	
	
	public VCTreeNode(Contact contact){
		this.contact = contact;
		nodeName = contact.getUsername();
	}
	public VCTreeNode(String nodeName){
		this.nodeName = nodeName;
	}
		
	public String toString(){
		return nodeName;
	}	
	
	public Contact getContact(){
		return contact;
	}
	public String getNodeName(){
		return nodeName;
	}
}

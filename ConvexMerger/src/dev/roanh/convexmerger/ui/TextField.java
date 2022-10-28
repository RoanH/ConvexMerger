package dev.roanh.convexmerger.ui;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.event.ChangeListener;

/**
 * Text field UI component.
 * @author Roan
 */
public class TextField{
	/**
	 * The accent color for this text field.
	 */
	private Color color;
	/**
	 * Text field bounds.
	 */
	private Rectangle2D bounds = new Rectangle2D.Double();
	/**
	 * Current text field text.
	 */
	private String text = "";
	/**
	 * Whether this text field has focus.
	 */
	private boolean focus = false;
	/**
	 * True if text in this text field is centred.
	 */
	private boolean center = false;
	/**
	 * Listener called when the text field text changes.
	 */
	private TextChangeListener changeListener = s->{};
	/**
	 * Listener called when this text field loses focus.
	 */
	private FocusLossListener focusListener = ()->{};
	/**
	 * The foreground (text) colour.
	 */
	private Color foreground = Theme.BOX_TEXT_COLOR;
	
	/**
	 * Constructs a new text field with the given accent color.
	 * @param color The accent color.
	 */
	public TextField(Color color){
		this.color = color;
	}
	
	/**
	 * Sets whether the text in this text field should be centred.
	 * @param center True to center the text in this text field.
	 */
	public void setCentred(boolean center){
		this.center = center;
	}
	
	/**
	 * Sets the change listener for this text field.
	 * @param listener The new change listener.
	 * @see ChangeListener
	 */
	public void setChangeListener(TextChangeListener listener){
		changeListener = listener;
	}
	
	/**
	 * Sets the focus loss listener for this text field.
	 * @param listener The new focus listener.
	 * @see FocusLossListener
	 */
	public void setFocusListener(FocusLossListener listener){
		focusListener = listener;
	}
	
	/**
	 * Sets the foreground (text) colour for this text field.
	 * @param color The new foreground colour.
	 */
	public void setForegroundColor(Color color){
		foreground = color;
	}
	
	/**
	 * Gets the text in this text field.
	 * @return The text in this text field.
	 */
	public String getText(){
		return text;
	}
	
	/**
	 * Checks if this text field currently has focus.
	 * @return True if this text field has focus.
	 */
	public boolean hasFocus(){
		return focus;
	}
	
	/**
	 * Sets the text for this text field.
	 * @param text The new text or <code>
	 *        null</code> to clear the text.
	 */
	public void setText(String text){
		this.text = text == null ? "" : text;
	}
	
	/**
	 * Removes the focus from this text field.
	 */
	public void removeFocus(){
		boolean old = focus;
		focus = false;
		if(old){
			focusListener.onFocusLost();
		}
	}
	
	/**
	 * Gives focus to this text field.
	 */
	public void giveFocus(){
		focus = true;
	}
	
	/**
	 * Handles a key event on this text field.
	 * @param event The event to handle.
	 */
	public void handleKeyEvent(KeyEvent event){
		if(hasFocus()){
			if(event.getKeyCode() == KeyEvent.VK_BACK_SPACE){
				if(!text.isEmpty()){
					text = text.substring(0, text.length() - 1);
					changeListener.onTextChange(text);
				}
			}else if(event.isControlDown() && event.getKeyCode() == KeyEvent.VK_V){
				try{
					text += Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
					changeListener.onTextChange(text);
				}catch(Exception ignore){
					//copy paste just fails
				}
			}else if(event.isControlDown() && event.getKeyCode() == KeyEvent.VK_C){
				try{
					Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), null);
				}catch(Exception ignore){
					//copy paste just fails
				}
			}else if(!event.isControlDown() && !event.isAltDown() && event.getKeyChar() != KeyEvent.CHAR_UNDEFINED){
				text += event.getKeyChar();
				changeListener.onTextChange(text);
			}
		}
	}
	
	/**
	 * Handles a mouse event on this text field.
	 * @param loc The location that was clicked.
	 */
	public void handleMouseClick(Point2D loc){
		boolean old = focus;
		focus = bounds.contains(loc);
		if(old){
			focusListener.onFocusLost();
		}
	}
	
	/**
	 * Renders this text field.
	 * @param g The graphics context to use.
	 * @param x The x coordinate of the top left corner.
	 * @param y The y coordinate of the top left corner.
	 * @param width The width of the text field.
	 * @param height The height of the text field.
	 */
	protected void render(Graphics2D g, double x, double y, double width, double height){
		g.setColor(Theme.DOUBLE_LIGHTEN);
		bounds = new Rectangle2D.Double(x, y, width, height);
		g.fill(bounds);
		g.setClip(bounds);
		
		g.setStroke(Theme.BORDER_STROKE);
		g.setColor(foreground);
		g.setFont(Theme.PRIDI_MEDIUM_14);
		FontMetrics fm = g.getFontMetrics();
		if(center){
			g.drawString(text, (float)(x + (width - fm.stringWidth(text)) / 2.0D), (float)(y + height - fm.getMaxDescent()));
		}else{
			g.drawString(text, (float)(x + 4.0F), (float)(y + height - fm.getMaxDescent()));
		}
		if(focus && ((System.currentTimeMillis() / 600) % 2 == 0)){
			int lx = (int)Math.ceil(x + (center ? (width + fm.stringWidth(text)) / 2.0D : 4.0F + fm.stringWidth(text)));
			g.setColor(color);
			g.drawLine(lx, (int)(y + 2.0F), lx, (int)(y + height - 4.0F));
		}
		
		g.setColor(color);
		g.draw(new Line2D.Double(x, y + height - 1, x + width - 1, y + height - 1));
		g.setClip(null);
	}
	
	/**
	 * Listener called when the text field context changes.
	 * @author Roan
	 */
	@FunctionalInterface
	public static abstract interface TextChangeListener{
		
		/**
		 * Called when the text field content changes.
		 * @param text The next text field content.
		 */
		public abstract void onTextChange(String text);
	}
	
	/**
	 * Listener called when the user stops editing a text field.
	 * @author Roan
	 */
	@FunctionalInterface
	public static abstract interface FocusLossListener{
		
		/**
		 * Called when the text field loses user focus.
		 */
		public abstract void onFocusLost();
	}
}

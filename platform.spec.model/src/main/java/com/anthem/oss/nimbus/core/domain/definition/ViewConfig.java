/**
 * 
 */
package com.anthem.oss.nimbus.core.domain.definition;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Soham Chakravarti
 *
 */
public class ViewConfig {
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(value={ElementType.ANNOTATION_TYPE})
	@Inherited
	public @interface ViewParamBehavior {
		
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(value={ElementType.ANNOTATION_TYPE})
	@Inherited
	public @interface ViewStyle {
		
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	@ViewParamBehavior
	public @interface Hints {
		public enum AlignOptions {
			Left,
			Right,
			Center,
			Inherit;
		}
		AlignOptions value() default AlignOptions.Inherit;
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	@ViewParamBehavior
	public @interface Mode {
		public enum Options {
			ReadOnly,
			Hidden,
			Inherit
		}
		Options value() default Options.Inherit;
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	@ViewStyle
	public @interface Page {
		public enum Type {
			Home,
			Details,
			Form,
			Static
		}
		String alias() default "page";
		Type type() default Type.Home;
		String route() default "";
		String breadCrumb() default "none";
		String imgSrc() default "";
		String styleClass() default "";
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	@ViewStyle
	public @interface Card {
		public enum Size {
			XSmall,
			Small,
			Medium,
			Large
		}
		String alias() default "Card";
		String imgSrc() default "";
		String title() default "";
		Size size() default Size.Large;
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	@ViewStyle
	public @interface CardDetailsGrid {
		String alias() default "CardDetailsGrid";
		String editUrl() default "";
		boolean draggable() default false;
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	@ViewStyle
	public @interface CardDetail {
		String alias() default "CardDetail";
		String cssClass() default "contentBox right-gutter bg-light";
		String imgSrc() default "";
		
		@Retention(RetentionPolicy.RUNTIME)
		@Target({ElementType.FIELD})
		@ViewStyle
		public @interface Header {
			String alias() default "CardDetailsHeader";
			String cssClass() default "";
		}
		
		@Retention(RetentionPolicy.RUNTIME)
		@Target({ElementType.FIELD})
		@ViewStyle
		public @interface Body {
			String alias() default "CardDetailsBody";
			String cssClass() default "";
		}
		
		@Retention(RetentionPolicy.RUNTIME)
		@Target({ElementType.FIELD})
		@ViewStyle
		public @interface Footer {
			String alias() default "CardDetailsFooter";
			String cssClass() default "";
		}
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	@ViewStyle
	public @interface FieldValue {
		String alias() default "FieldValue";
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	@ViewStyle
	public @interface Section {
		
		public enum Type {
			HEADER,
			FOOTER,
			LEFTBAR,
			RIGHTBAR,
			DEFAULT;
		}
		
		Type value() default Type.DEFAULT;
		String alias() default "Section";
		String imgSrc() default "";
		String cssClass() default "";
	}
		
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	@ViewStyle
	public @interface Form {
		String alias() default "Form";
		String submitUrl() default "";
		String b() default "";
		String cssClass() default "";
		boolean submitButton() default true;
	}	
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	@ViewStyle
	public @interface ButtonGroup {	
		String alias() default "ButtonGroup";
		String cssClass() default "";
	}
	
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	@ViewStyle
	public @interface Button {	
		String alias() default "Button";
		String url() default "";
		String b() default "";
		String method() default "GET";
		String imgSrc() default "";
		String type() default "submit";
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	@ViewStyle
	public @interface Menu {
		public enum Type {
			CONTEXT;
		}
		Type value() default Type.CONTEXT;
		String alias() default "Menu";		
		String cssClass() default "";
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	@ViewStyle
	public @interface Link {
		public enum Type {
			LOGO,
			APPTITLE,
			MENU,
			DEFAULT;
		}
		Type value() default Type.DEFAULT;
		String url() default "";
		String method() default "GET";
		String b() default "$executeAnd$nav";
		String imgSrc() default "";
		String styleClass() default "";
		String altText() default "";
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	@ViewStyle
	public @interface TextBox {
		String alias() default "TextBox";
		boolean hidden() default false;
		boolean readOnly() default false;
		String help() default "";
		String labelClass() default "anthem-label";
		String type() default "text";
		boolean postEventOnChange() default false;
		String controlId() default "";
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	@ViewStyle
	public @interface ComboBox {
		String alias() default "ComboBox";
		boolean readOnly() default false;
		String labelClass() default "anthem-label";
		boolean postEventOnChange() default false;
		String controlId() default "";
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	@ViewStyle
	public @interface StaticText {
		String alias() default "StaticText";
		String contentId() default "";
	}	
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	@ViewStyle
	public @interface MultiSelect {
		String alias() default "MultiSelect";
		String labelClass() default "anthem-label";
		boolean postEventOnChange() default false;
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	@ViewStyle
	public @interface InputDate {
		String alias() default "InputDate";
		boolean readOnly() default false;	
		String labelClass() default "anthem-label";
		String type() default "date";
		boolean postEventOnChange() default false;
		String controlId() default "";
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	@ViewStyle
	public @interface Grid {
		String alias() default "Grid";
		boolean onLoad() default false;
		String url() default "";
		boolean rowSelection() default false;
		String pageSize() default "10";
		boolean pagination() default true;
		boolean postButton() default false;
		String postButtonUrl() default "";
		boolean postEventOnChange() default false;
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	@ViewStyle
	public @interface Radio {
		String alias() default "Radio";
		String labelClass() default "anthem-label";
		String level() default "0";
		String cssClass() default "";
		boolean postEventOnChange() default false;
		String controlId() default "";
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	@ViewStyle
	public @interface CheckBox {
		String alias() default "CheckBox";
		String level() default "0";
		String cssClass() default "";
		String labelClass() default "anthem-label";
		boolean postEventOnChange() default false;
		String controlId() default "";
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	@ViewStyle
	public @interface MultiSelectCard {
		String alias() default "MultiSelectCard";
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	@ViewStyle
	public @interface MultiGrid {
		String alias() default "MultiGrid";
		String level() default "0";
		String header() default "test";
		String cssClass() default "question-header";
		boolean postEventOnChange() default false;
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	@ViewStyle
	public @interface GlobalSection {
		String alias() default "globalSection";
		String imgSrc() default "";
		String styleClass() default "";
	}
		
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	@ViewStyle
	public @interface Summary {
		String alias() default "summary";
		String imgSrc() default "";
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	@ViewStyle
	public @interface BreadCrumb {
		String label();
		int order();
		String alias() default "breadCrumb";
		boolean postEventOnChange() default false;
	}	

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	@ViewStyle
	public @interface LinearGauge {
		String alias() default "LinearGauge";
		String labelClass() default "anthem-label";
		boolean postEventOnChange() default false;
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	@ViewStyle
	public @interface ContentContainer {
		String alias() default "ContentContainer";
		String content() default "";
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	@ViewStyle
	public @interface SubHeader {
		String alias() default "SubHeader";
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD})
	@ViewStyle
	public @interface PickList {
		String alias() default "PickList";
		String labelClass() default "anthem-label";
		boolean readOnly() default false;
		String cssClass() default "";
		boolean postEventOnChange() default false;
		String sourceHeader() default "SourceList";
		String targetHeader() default "TargetList";
	}
	
}

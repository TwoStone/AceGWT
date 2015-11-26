package edu.ycp.cs.dh.acegwt.client.demo.client;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import edu.ycp.cs.dh.acegwt.client.ace.*;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class AceGWTDemo implements EntryPoint {
	private AceEditor editor1;
	private AceEditor editor2;
	private InlineLabel rowColLabel;
	private InlineLabel absolutePositionLabel;
	private TextBox commandLine;

	private static final String JAVA_TEXT =
			"public class Hello {\n" +
			"\tpublic static void main(String[] args) {\n" +
			"\t\tSystem.out.println(\"Hello, world!\");\n" +
			"\t}\n" +
			"}\n";

	private static class MyCompletionProvider implements AceCompletionProvider {
		@Override

		public void getProposals(final AceEditor editor, final AceEditorCursorPosition pos, final String prefix, final AceCompletionCallback callback) {
			GWT.log("sending completion proposals");
			callback.invokeWithCompletions(new AceCompletion[]{
					new AceCompletionValue("first", "firstcompletion", "custom", 10),
					new AceCompletionValue("second", "secondcompletion", "custom", 11),
					new AceCompletionValue("third", "thirdcompletion", "custom", 12),
					new AceCompletionSnippet("fourth (snippets)",
							new AceCompletionSnippetSegment[]{
							new AceCompletionSnippetSegmentLiteral("filler_"),
							new AceCompletionSnippetSegmentTabstopItem("tabstop1"),
							new AceCompletionSnippetSegmentLiteral("_\\filler_"), // putting backslash in here to prove escaping is working
							new AceCompletionSnippetSegmentTabstopItem("tabstop2"),
							new AceCompletionSnippetSegmentLiteral("_$filler_"), // putting dollar in here to prove escaping is working
							new AceCompletionSnippetSegmentTabstopItem("tabstop3"),
							new AceCompletionSnippetSegmentLiteral("\nnextlinefiller_"),
							new AceCompletionSnippetSegmentTabstopItem("tabstop}4"),
							new AceCompletionSnippetSegmentLiteral("_filler_"),
							new AceCompletionSnippetSegmentTabstopItem("") /* Empty tabstop -- tab to end of replacement text */
					},"csnip", "Write a new snippet in your editor", 14)
			});
		}
	}

	/**
	 * This is the entry point method.
	 */
	@Override
    public void onModuleLoad() {

		// create first AceEditor widget
		this.editor1 = new AceEditor();
		this.editor1.setWidth("800px");
		this.editor1.setHeight("300px");

		// create second AceEditor widget
		this.editor2 = new AceEditor();
		this.editor2.setWidth("800px");
		this.editor2.setHeight("300px");

		// Try out custom code completer
		AceEditor.addCompletionProvider(new MyCompletionProvider());

		// build the UI
		this.buildUI();

		// start the first editor and set its theme and mode
		this.editor1.startEditor(); // must be called before calling setTheme/setMode/etc.
		this.editor1.setTheme(AceEditorTheme.ECLIPSE);
		this.editor1.setMode(AceEditorMode.JAVA);

		// use cursor position change events to keep a label updated
		// with the current row/col
		this.editor1.addOnCursorPositionChangeHandler(new AceEditorCallback() {
			@Override
			public void invokeAceCallback(final JavaScriptObject obj) {
				AceGWTDemo.this.updateEditor1CursorPosition();
			}
		});
		this.editor1.getSelection().addSelectionListener(new AceSelectionListener() {
			@Override
			public void onChangeSelection(final AceSelection selection) {
				AceGWTDemo.this.updateEditor1CursorPosition();
			}
		});
		this.updateEditor1CursorPosition(); // initial update

		// set some initial text in editor 1
		this.editor1.setText(JAVA_TEXT);

		// add some annotations
		this.editor1.addAnnotation(0, 1, "What's up?", AceAnnotationType.WARNING);
		this.editor1.addAnnotation(2, 1, "This code is lame", AceAnnotationType.ERROR);
		this.editor1.setAnnotations();
		this.editor1.initializeCommandLine(new AceDefaultCommandLine(this.commandLine));
		this.editor1.addCommand(new AceCommandDescription("increaseFontSize",
				new AceCommandDescription.ExecAction() {
			@Override
			public Object exec(final AceEditor editor) {
				final int fontSize = editor.getFontSize();
				editor.setFontSize(fontSize + 1);
				return null;
			}
		}).withBindKey("Ctrl-=|Ctrl-+"));
		this.editor1.addCommand(new AceCommandDescription("decreaseFontSize",
				new AceCommandDescription.ExecAction() {
			@Override
			public Object exec(final AceEditor editor) {
				int fontSize = editor.getFontSize();
				fontSize = Math.max(fontSize - 1, 1);
				editor.setFontSize(fontSize);
				return null;
			}
		}).withBindKey("Ctrl+-|Ctrl-_"));
		this.editor1.addCommand(new AceCommandDescription("resetFontSize",
				new AceCommandDescription.ExecAction() {
			@Override
			public Object exec(final AceEditor editor) {
				editor.setFontSize(12);
				return null;
			}
		}).withBindKey("Ctrl+0|Ctrl-Numpad0"));
		final AceCommandDescription gotolineCmd = this.editor1.getCommandDescription("gotoline");
		this.editor1.addCommand(
				new AceCommandDescription("gotoline2", gotolineCmd.getExec())
				.withBindKey("Alt-1").withReadOnly(true));

		// start the second editor and set its theme and mode
		this.editor2.startEditor();
		this.editor2.setTheme(AceEditorTheme.TWILIGHT);
		this.editor2.setMode(AceEditorMode.PERL);
	}

	/**
	 * This method builds the UI.
	 * It creates UI widgets that exercise most/all of the AceEditor methods,
	 * so it's a bit of a kitchen sink.
	 */
	private void buildUI() {
		final VerticalPanel mainPanel = new VerticalPanel();
		mainPanel.setWidth("100%");

		mainPanel.add(new Label("Label above!"));

		mainPanel.add(this.editor1);

		// Label to display current row/column
		this.rowColLabel = new InlineLabel("");
		mainPanel.add(this.rowColLabel);

		// Label to display current absolute position
		this.absolutePositionLabel = new InlineLabel("");
		mainPanel.add(this.absolutePositionLabel);

		// Create some buttons for testing various editor APIs
		final HorizontalPanel buttonPanel = new HorizontalPanel();

		// Add button to insert text at current cursor position
		final Button insertTextButton = new Button("Insert");
		insertTextButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				//Window.alert("Cursor at: " + editor1.getCursorPosition());
				AceGWTDemo.this.editor1.insertAtCursor("inserted text!");
			}
		});
		buttonPanel.add(insertTextButton);

		// Add check box to enable/disable soft tabs
		final CheckBox softTabsBox = new CheckBox("Soft tabs");
		softTabsBox.setValue(true); // I think soft tabs is the default
		softTabsBox.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				AceGWTDemo.this.editor1.setUseSoftTabs(softTabsBox.getValue());
			}
		});
		buttonPanel.add(softTabsBox);

		// add text box and button to set tab size
		final TextBox tabSizeTextBox = new TextBox();
		tabSizeTextBox.setWidth("4em");
		final Button setTabSizeButton = new Button("Set tab size");
		setTabSizeButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				AceGWTDemo.this.editor1.setTabSize(Integer.parseInt(tabSizeTextBox.getText()));
			}
		});
		buttonPanel.add(new InlineLabel("Tab size"));
		buttonPanel.add(tabSizeTextBox);
		buttonPanel.add(setTabSizeButton);

		// add text box and button to go to a given line
		final TextBox gotoLineTextBox = new TextBox();
		gotoLineTextBox.setWidth("4em");
		final Button gotoLineButton = new Button("Go to line");
		gotoLineButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				AceGWTDemo.this.editor1.gotoLine(Integer.parseInt(gotoLineTextBox.getText()));
			}
		});
		buttonPanel.add(new InlineLabel("Go to line"));
		buttonPanel.add(gotoLineTextBox);
		buttonPanel.add(gotoLineButton);

		// checkbox to set whether or not horizontal scrollbar is always visible
		final CheckBox hScrollBarAlwaysVisibleBox = new CheckBox("H scrollbar");
		hScrollBarAlwaysVisibleBox.setValue(true);
		hScrollBarAlwaysVisibleBox.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				AceGWTDemo.this.editor1.setHScrollBarAlwaysVisible(hScrollBarAlwaysVisibleBox.getValue());
			}
		});
		buttonPanel.add(hScrollBarAlwaysVisibleBox);

		// checkbox to show/hide gutter
		final CheckBox showGutterBox = new CheckBox("Show gutter");
		showGutterBox.setValue(true);
		showGutterBox.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				AceGWTDemo.this.editor1.setShowGutter(showGutterBox.getValue());
			}
		});
		buttonPanel.add(showGutterBox);

		// checkbox to set/unset readonly mode
		final CheckBox readOnlyBox = new CheckBox("Read only");
		readOnlyBox.setValue(false);
		readOnlyBox.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				AceGWTDemo.this.editor1.setReadOnly(readOnlyBox.getValue());
			}
		});
		buttonPanel.add(readOnlyBox);

		// checkbox to show/hide print margin
		final CheckBox showPrintMarginBox = new CheckBox("Show print margin");
		showPrintMarginBox.setValue(true);
		showPrintMarginBox.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				AceGWTDemo.this.editor1.setShowPrintMargin(showPrintMarginBox.getValue());
			}
		});
		buttonPanel.add(showPrintMarginBox);

		// checkbox to enable/disable autocomplete
		final CheckBox enableAutocompleteBox = new CheckBox("Enable autocomplete");
		enableAutocompleteBox.setValue(false);
		enableAutocompleteBox.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				AceGWTDemo.this.editor1.setAutocompleteEnabled(enableAutocompleteBox.getValue());
			}
		});
		buttonPanel.add(enableAutocompleteBox);

		// Test for AceEditor.getRow
		final Button clickMe = new Button("Click me!");
		clickMe.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				final AceEditorCursorPosition pos = AceGWTDemo.this.editor1.getCursorPosition();
				final String line = AceGWTDemo.this.editor1.getLine(pos.getRow());
				final AceEditorCursorPosition pos10 = AceGWTDemo.this.editor1.getPositionFromIndex(10);
				Window.alert("Index 10=" + pos10 + ", cur line=" + line);
			}
		});
		buttonPanel.add(clickMe);

		// Test for AceEditor Markers removal
		final Button removeMarks = new Button("Add/Remove Markers");
		removeMarks.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				if (AceGWTDemo.this.editor1.getMarkers().isEmpty()) {
					// add some markers
					AceGWTDemo.this.editor1.addMarker(AceRange.create(0, 0, 1, 41), "ace_selection", AceMarkerType.FULL_LINE, false);
					AceGWTDemo.this.editor1.addFloatingMarker(AceRange.create(2, 2, 2, 38), "ace_selected-word", AceMarkerType.TEXT);
				} else {
					Window.alert("Removing " + AceGWTDemo.this.editor1.getMarkers() + " markers");
					AceGWTDemo.this.editor1.removeAllMarkers();
				}
			}
		});
		buttonPanel.add(removeMarks);

		mainPanel.add(buttonPanel);

		final HorizontalPanel buttonPanel2 = new HorizontalPanel();
		buttonPanel2.add(new InlineLabel("Command line"));
		this.commandLine = new TextBox();
		buttonPanel2.add(this.commandLine);
		mainPanel.add(buttonPanel2);

		mainPanel.add(this.editor2);
		mainPanel.add(new Label("Label below!"));

		// Demo button for get number of lines
		final Button appendLineCount = new Button("Append Line Count");
		appendLineCount.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				final String message = AceGWTDemo.this.editor2.getText() + "There are "
						+ AceGWTDemo.this.editor1.getLineCount() + " lines in the main editor.";
				AceGWTDemo.this.editor2.setText(message);
			}

		});
		buttonPanel2.add(appendLineCount);

		final Button flipFocus = new Button("Focus 1st Editor");
		flipFocus.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				AceGWTDemo.this.editor1.focus();
			}

		});
		buttonPanel2.add(flipFocus);

		final Button flipFocus2 = new Button("Focus 2nd Editor");
		flipFocus2.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				AceGWTDemo.this.editor2.focus();
			}

		});
		buttonPanel2.add(flipFocus2);

		final com.extjs.gxt.ui.client.widget.ContentPanel panel = new ContentPanel();
		panel.add(mainPanel);

		final com.extjs.gxt.ui.client.widget.Window w = new com.extjs.gxt.ui.client.widget.Window();
		w.setModal(true);
		w.add(panel);
		w.show();
	}

	private void updateEditor1CursorPosition() {
		final AceEditorCursorPosition cursorPosition = this.editor1.getCursorPosition();
		String selectionAnchorPosText = "";
		if (!this.editor1.getSelection().isEmpty()) {
			selectionAnchorPosText += this.editor1.getSelection().getSelectionAnchor() + " - ";
		}
		this.rowColLabel.setText(selectionAnchorPosText + cursorPosition.toString());


		String selectionAnchorIndText = "";
		if (!this.editor1.getSelection().isEmpty()) {
			selectionAnchorIndText += this.editor1.getIndexFromPosition(
					this.editor1.getSelection().getSelectionAnchor()) + " - ";
		}
		final int absPos = this.editor1.getIndexFromPosition(cursorPosition);
		this.absolutePositionLabel.setText(selectionAnchorIndText + String.valueOf(absPos));
	}
}

/*******************************************************************************
 * Copyright (c) 2008, 2020 Symbian Software Systems and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Andrew Ferguson (Symbian) - Initial implementation
 *     Marco Stornelli <marco.stornelli@gmail.com> - Bug 333134
 *     Alexander Fedorov <alexander.fedorov@arsysop.ru> - ongoing support
 *******************************************************************************/
package org.eclipse.cdt.ui.dialogs;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.cdt.core.options.OptionMetadata;
import org.eclipse.cdt.core.options.OptionStorage;
import org.eclipse.cdt.doxygen.DoxygenMetadata;
import org.eclipse.cdt.internal.ui.text.doctools.DocCommentOwnerManager;
import org.eclipse.cdt.internal.ui.text.doctools.NullDocCommentOwner;
import org.eclipse.cdt.ui.text.doctools.IDocCommentOwner;
import org.eclipse.cdt.utils.ui.controls.ControlFactory;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

/**
 * <em>This class is not intended for use outside of CDT</em>
 *
 * @noextend This class is not intended to be subclassed by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class DocCommentOwnerComposite extends Composite {
	/**
	 * @deprecated will throw {@link NullPointerException} on attempt to access
	 */
	@Deprecated
	protected DocCommentOwnerCombo fDocCombo;
	protected Label desc;
	protected Label comboLabel;
	protected Group group;

	private Combo combo;
	private final IDocCommentOwner fOwners[];

	private final Map<OptionMetadata<Boolean>, Button> buttons;

	public DocCommentOwnerComposite(Composite parent, IDocCommentOwner initialOwner, String description, String label) {
		super(parent, SWT.NONE);
		fOwners = getNontestOwners();
		buttons = new LinkedHashMap<>();
		GridLayout gl = new GridLayout();
		gl.marginHeight = gl.marginWidth = 0;
		setLayout(gl);

		group = ControlFactory.createGroup(this, DialogsMessages.DocCommentOwnerComposite_DocumentationToolGroupTitle,
				2);
		group.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());

		desc = new Label(group, SWT.WRAP);
		GridData gd = GridDataFactory.fillDefaults().grab(false, false).span(2, 1).create();
		gd.widthHint = 150;
		desc.setText(description);
		desc.setLayoutData(gd);

		comboLabel = new Label(group, SWT.NONE);
		comboLabel.setText(label);
		combo = createCombo(group, initialOwner);
		combo.addSelectionListener(SelectionListener.widgetSelectedAdapter(e -> recheckButtons()));
		selectDocumentOwner(initialOwner, combo);
	}

	private Combo createCombo(Composite parent, IDocCommentOwner initialOwner) {
		String[] items = new String[fOwners.length + 1];
		items[0] = DialogsMessages.DocCommentOwnerCombo_None;
		for (int i = 0; i < fOwners.length; i++) {
			items[i + 1] = fOwners[i].getName();
		}
		Combo created = ControlFactory.createSelectCombo(parent, items, DialogsMessages.DocCommentOwnerCombo_None);
		return created;
	}

	private void selectDocumentOwner(IDocCommentOwner owner, Combo created) {
		for (int i = 0; i < fOwners.length; i++) {
			if (fOwners[i].getID().equals(owner.getID())) {
				created.select(i + 1);
				return;
			}
		}
		created.select(0);
	}

	/**
	 * @return the array of registered doc-comment owners, filtering out those from the
	 * test plug-in.
	 */
	private IDocCommentOwner[] getNontestOwners() {
		List<IDocCommentOwner> result = new ArrayList<>();
		for (IDocCommentOwner owner : DocCommentOwnerManager.getInstance().getRegisteredOwners()) {
			if (owner.getID().indexOf(".test.") == -1) //$NON-NLS-1$
				result.add(owner);
		}
		return result.toArray(new IDocCommentOwner[result.size()]);
	}

	/**
	 * Creates widgets required to represent doxygen options, extracted from constructor to keep it unchanged.
	 * Needs to be invoked only once just after the constructor
	 *
	 * @param metadata the doxygen metadata to use for checkbox creation
	 *
	 * @since 6.7
	 *
	 * @noreference This method is not intended to be referenced by clients.
	 */
	public void setDoxygenMetadata(DoxygenMetadata metadata) {
		metadata.booleanOptions().forEach(o -> createCheckBox(group, o));
	}

	private Button createCheckBox(Composite parent, OptionMetadata<Boolean> option) {
		Button checkBox = new Button(parent, SWT.CHECK);
		checkBox.setText(option.name());
		checkBox.setToolTipText(option.description());
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalIndent = 0;
		gd.horizontalSpan = 2;
		checkBox.setLayoutData(gd);
		buttons.put(option, checkBox);
		return checkBox;
	}

	public IDocCommentOwner getSelectedDocCommentOwner() {
		int index = combo.getSelectionIndex();
		return index == 0 ? NullDocCommentOwner.INSTANCE : fOwners[index - 1];
	}

	/**
	 * Initializes widget values from the given option storage instance
	 *
	 * @param storage the option storage to initialize from
	 *
	 * @since 6.7
	 *
	 * @noreference This method is not intended to be referenced by clients.
	 */
	public void initialize(OptionStorage storage) {
		buttons.entrySet().stream().forEach(e -> e.getValue().setSelection(storage.load(e.getKey())));
	}

	/**
	 * Apply widget values to the given option storage instance
	 *
	 * @param storage the option storage to apply to
	 *
	 * @since 6.7
	 *
	 * @noreference This method is not intended to be referenced by clients.
	 */
	public void apply(OptionStorage storage) {
		buttons.entrySet().stream().forEach(e -> storage.save(e.getValue().getSelection(), e.getKey()));
	}

	@Override
	public void setEnabled(boolean enabled) {
		desc.setEnabled(enabled);
		comboLabel.setEnabled(enabled);
		combo.setEnabled(enabled);
		group.setEnabled(enabled);
		recheckButtons();
	}

	void recheckButtons() {
		boolean doxygenEnabled = combo.isEnabled()
				&& DocCommentOwnerManager.DOXYGEN_CDT_DOC_ONWER_ID.equals(getSelectedDocCommentOwner().getID());
		buttons.values().forEach(b -> b.setEnabled(doxygenEnabled));
	}

}

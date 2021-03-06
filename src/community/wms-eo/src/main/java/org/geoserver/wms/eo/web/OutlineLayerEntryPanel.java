/* Copyright (c) 2001 - 2013 OpenPlans - www.openplans.org. All rights reserved.
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wms.eo.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.convert.IConverter;
import org.geoserver.catalog.LayerGroupInfo;
import org.geoserver.catalog.LayerInfo;
import org.geoserver.catalog.PublishedInfo;
import org.geoserver.catalog.StyleInfo;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.web.data.resource.ResourceConfigurationPage;
import org.geoserver.wms.eo.EoLayerType;


/**
 * Allows to edit the Outline Layer
 */
@SuppressWarnings("serial")
public class OutlineLayerEntryPanel extends Panel {

    private LayerInfo layer;
    private StyleInfo layerStyle;
    
    
    @SuppressWarnings({ "rawtypes" })
    public OutlineLayerEntryPanel(String id, final Form form, WorkspaceInfo workspace) {
        super(id);
        setOutputMarkupId(true);
        
        LayerGroupInfo group = (LayerGroupInfo) form.getModel().getObject();
        
        int pos = 0;
        for (PublishedInfo p : group.getLayers()) {
            if (p instanceof LayerInfo) {
                if (EoLayerType.COVERAGE_OUTLINE.name().equals(((LayerInfo) p).getMetadata().get(EoLayerType.KEY))) {
                    layer = (LayerInfo) p;
                    layerStyle = group.getStyles().get(pos);
                }
            }
            pos++;
        }
        
        Link link = new Link("outlineLayer") {
            @Override
            public void onClick() {
                Map<String,String> params = new HashMap<String,String>(2);
                if (layer.getResource().getStore().getWorkspace() != null) {
                    params.put(ResourceConfigurationPage.WORKSPACE, layer.getResource().getStore().getWorkspace().getName());
                }
                params.put(ResourceConfigurationPage.NAME, layer.getName());
                setResponsePage(ResourceConfigurationPage.class, new PageParameters(params));
            }            
        };
        link.add(new Label("outlineLayerName", new PropertyModel(layer, "name")));
        add(link);        

        // available styles
        List<StyleInfo> styles = new ArrayList<StyleInfo>(layer.getStyles());
        Collections.sort(styles,  new StyleInfoNameComparator());
        DropDownChoice<StyleInfo> styleField = new DropDownChoice<StyleInfo>("outlineLayerStyle",  new PropertyModel<StyleInfo>(this, "layerStyle"), styles) {
            @Override
            public IConverter getConverter(Class<?> type) { 
                return form.getConverter(type);
            }             
        };
        styleField.setNullValid(true);
        styleField.setOutputMarkupId(true);        
        add(styleField);
    }
    
    public void setLayer(LayerInfo layer) {
        this.layer = layer;
    }
    
    public void setLayerStyle(StyleInfo layerStyle) {
        this.layerStyle = layerStyle;
    }
    
    public LayerInfo getLayer() {
        return layer;
    }
    
    public StyleInfo getLayerStyle() {
        return layerStyle;
    }     
}
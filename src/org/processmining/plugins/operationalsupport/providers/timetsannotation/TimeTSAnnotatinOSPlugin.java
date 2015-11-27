package org.processmining.plugins.operationalsupport.providers.timetsannotation;

import org.deckfour.xes.extension.std.XExtendedEvent;
import org.deckfour.xes.extension.std.XLifecycleExtension.StandardModel;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.impl.XEventImpl;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.connections.ConnectionManager;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.plugin.events.Logger.MessageLevel;
import org.processmining.framework.util.socket.ServiceEnvironment;
import org.processmining.models.graphbased.directed.transitionsystem.payload.event.EventPayloadTransitionSystem;
import org.processmining.models.operationalsupport.Analysis;
import org.processmining.models.operationalsupport.Prediction;
import org.processmining.models.operationalsupport.ProviderResponse;
import org.processmining.models.operationalsupport.Recommendation;
import org.processmining.models.operationalsupport.Request;
import org.processmining.models.operationalsupport.net.provider.Provider;
import org.processmining.models.operationalsupport.net.provider.impl.AbstractProvider;
import org.processmining.models.operationalsupport.net.service.OSService;
import org.processmining.plugins.tsanalyzer.TimeTransitionSystemAnnotation;
import org.processmining.plugins.tsanalyzer.TimeTransitionSystemAnnotationConnection;
import org.processmining.plugins.tsanalyzer.annotation.TransitionSystemAnnotationConnection;

// @Plugin(name = "Operational Support Time Annotation Provider",
// parameterLabels = { "Operational Support Service" }, returnLabels = {
// "TS Time Annotation Provider" }, returnTypes = { AbstractProvider.class },
// userAccessible = true)
@Plugin(name = "Operational Support Annotation Provider", parameterLabels = { "Operational Support Service",
		"Transition System Time Annotation" }, returnLabels = { "Time Annotation Provider" }, returnTypes = { AbstractProvider.class }, userAccessible = true)
public class TimeTSAnnotatinOSPlugin extends AbstractProvider {

	private final TimeTSAnnotationOS timeAnotationOS;

	public TimeTSAnnotatinOSPlugin(OSService owner, final TimeTransitionSystemAnnotation tsAnnotation,
			final EventPayloadTransitionSystem transitionSystem) {
		super(owner);
		timeAnotationOS = new TimeTSAnnotationOS(tsAnnotation, transitionSystem, new ConfidenceIntervalStdDev(2));
	}

	public void populateResponse(ServiceEnvironment environment, Request request, ProviderResponse response) {

		environment.log("Operational Support TS Time Annotation is processing request " + request.getId());

		Analysis a = timeAnotationOS.getElapsedAnalysts(request);
		if (a != null) {
			response.addAnalysis(a);
		}

		Prediction p = timeAnotationOS.getPrediction(request);
		response.addPrediction(p);
		for (Recommendation rec : timeAnotationOS.getRecommendations(request)) {
			response.addRecommendation(rec);
		}
	}

	protected XEvent generateEvent(String task, StandardModel transition) {
		XEvent event = new XEventImpl();
		XExtendedEvent ate = XExtendedEvent.wrap(event);
		ate.setStandardTransition(transition);
		ate.setName(task);
		return event;
	}

	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "M. Pesic", email = "m.pesic@tue.nl", uiLabel = UITopiaVariant.USEPLUGIN)
	@PluginVariant(variantLabel = "Default settings", requiredParameterLabels = { 0, 1 })
	public static Provider registerServiceProviderAUI(final UIPluginContext context, OSService service,
			TimeTransitionSystemAnnotation a) {
		return registerServiceProviderA(context, service, a);
	}

	@PluginVariant(variantLabel = "Default settings", requiredParameterLabels = { 0, 1 })
	public static Provider registerServiceProviderA(final PluginContext context, OSService service,
			TimeTransitionSystemAnnotation tsAnnotation) {
		EventPayloadTransitionSystem ts = null;
		ConnectionManager cm = context.getConnectionManager();
		try {
			for (TimeTransitionSystemAnnotationConnection tsc : cm.getConnections(
					TimeTransitionSystemAnnotationConnection.class, context, tsAnnotation)) {
				ts = tsc.getObjectWithRole(TransitionSystemAnnotationConnection.TS);
			}
		} catch (ConnectionCannotBeObtained e) {
			context.log("The transition system for this annotation was not found.", MessageLevel.ERROR);
		}

		TimeTSAnnotatinOSPlugin dummy = new TimeTSAnnotatinOSPlugin(service, tsAnnotation, ts);
		context.getFutureResult(0).setLabel(context.getFutureResult(0).getLabel());

		return dummy;
	}
}
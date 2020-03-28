//package com.minebunch.kitpvp.managers;
//
//import com.minebunch.kitpvp.KitPlugin;
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import org.bukkit.World;
//import org.bukkit.entity.Entity;
//import org.bukkit.event.Cancellable;
//import org.bukkit.event.Event;
//import org.bukkit.event.EventHandler;
//import org.bukkit.event.EventPriority;
//import org.bukkit.event.Listener;
//import org.bukkit.event.block.BlockEvent;
//import org.bukkit.event.entity.EntityEvent;
//import org.bukkit.event.entity.PlayerLeashEntityEvent;
//import org.bukkit.event.hanging.HangingEvent;
//import org.bukkit.event.inventory.InventoryClickEvent;
//import org.bukkit.event.inventory.InventoryCloseEvent;
//import org.bukkit.event.inventory.InventoryCreativeEvent;
//import org.bukkit.event.inventory.InventoryDragEvent;
//import org.bukkit.event.inventory.InventoryInteractEvent;
//import org.bukkit.event.inventory.InventoryOpenEvent;
//import org.bukkit.event.inventory.InventoryPickupItemEvent;
//import org.bukkit.event.player.PlayerEvent;
//import org.bukkit.event.vehicle.VehicleEvent;
//import org.bukkit.event.weather.WeatherEvent;
//import org.bukkit.event.world.WorldEvent;
//import org.reflections.Reflections;
//
//public class PerWorldListenerManager {
//	private final Map<World, List<PerWorldListenerData>> listeners = new HashMap<>();
//	private Comparator<MethodData> priorityComparator = (o1, o2) -> {
//		EventHandler e1 = o1.handler;
//		EventHandler e2 = o2.handler;
//
//		return e1.priority().ordinal() - e2.priority().ordinal();
//	};
//
//	public void enable() {
//		Reflections reflections = new Reflections("org.bukkit.event");
//		SorterListener listener = new SorterListener();
//		Set<Class<? extends Event>> eventClasses = reflections.getSubTypesOf(Event.class);
//
//		for (Class<? extends Event> clazz : eventClasses) {
//			try {
//				clazz.getDeclaredMethod("getHandlerList");
//			} catch (NoSuchMethodException e) {
//				e.printStackTrace();
//				continue;
//			}
//
//			KitPlugin.getInstance().getServer().getPluginManager().registerEvent(clazz, listener, EventPriority.NORMAL, (listener1, event) -> ((SorterListener) listener1).onEvent(event), KitPlugin.getInstance());
//		}
//	}
//
//	public void register(Listener listener, World world) {
//		List<PerWorldListenerData> list;
//		if (!listeners.containsKey(world)) {
//			listeners.put(world, list = new ArrayList<>());
//		} else {
//			list = listeners.get(world);
//		}
//
//		PerWorldListenerData data = new PerWorldListenerData();
//		data.listener = listener;
//		data.methodMap = new HashMap<>();
//
//		for (Method m : listener.getClass().getDeclaredMethods()) {
//			EventHandler handler = m.getAnnotation(EventHandler.class);
//
//			if (handler == null) {
//				continue;
//			}
//
//			if (m.getParameterTypes().length != 1) {
//				continue;
//			}
//
//			Class clazz = m.getParameterTypes()[0];
//			if (Event.class.isAssignableFrom(clazz)) {
//				m.setAccessible(true);
//
//				if (!data.methodMap.containsKey(clazz)) {
//					data.methodMap.put(clazz, new ArrayList<>());
//				}
//
//				List<Method> methodList = data.methodMap.get(clazz);
//				methodList.add(m);
//			}
//		}
//
//		list.add(data);
//	}
//
//	private void callEvent(World world, Event event) {
//		if (!listeners.containsKey(world)) {
//			return;
//		}
//
//		boolean checkCancellation = event instanceof Cancellable;
//		List<PerWorldListenerData> list = listeners.get(world);
//		List<MethodData> methodsToCall = new ArrayList<>();
//
//		for (PerWorldListenerData d : list) {
//			for (Class clazz = event.getClass(); Event.class.isAssignableFrom(clazz); clazz = clazz.getSuperclass()) {
//				if (!d.methodMap.containsKey(clazz)) {
//					continue;
//				}
//
//				List<Method> methods = d.methodMap.get(clazz);
//				for (Method m : methods) {
//					EventHandler handler = m.getAnnotation(EventHandler.class);
//					MethodData data = new MethodData();
//					data.handler = handler;
//					data.listener = d.listener;
//					data.method = m;
//
//					methodsToCall.add(data);
//				}
//			}
//		}
//
//		Collections.sort(methodsToCall, priorityComparator);
//
//		for (MethodData m : methodsToCall) {
//			if (checkCancellation && ((Cancellable) event).isCancelled() && m.handler.ignoreCancelled()) {
//				continue;
//			}
//
//			try {
//				m.method.invoke(m.listener, event);
//			} catch (IllegalAccessException | InvocationTargetException e) {
//				e.printStackTrace();
//			}
//		}
//	}
//
//	private class SorterListener implements Listener {
//		@EventHandler
//		public void onEvent(Event event) {
//			if (event == null) {
//				return;
//			}
//
//			World passTo = null;
//
//			if (event instanceof BlockEvent) {
//				passTo = ((BlockEvent) event).getBlock().getWorld();
//			} else if (event instanceof EntityEvent) {
//				Entity entity = ((EntityEvent) event).getEntity();
//
//				if (entity == null) {
//					return;
//				}
//
//				passTo = entity.getWorld();
//			} else if (event instanceof HangingEvent) {
//				passTo = ((HangingEvent) event).getEntity().getWorld();
//			} else if (event instanceof InventoryClickEvent) {
//				passTo = ((InventoryClickEvent) event).getWhoClicked().getWorld();
//			} else if (event instanceof InventoryCloseEvent) {
//				passTo = ((InventoryCloseEvent) event).getPlayer().getWorld();
//			} else if (event instanceof InventoryPickupItemEvent) {
//				passTo = ((InventoryPickupItemEvent) event).getItem().getWorld();
//			} else if (event instanceof InventoryCreativeEvent) {
//				passTo = ((InventoryCreativeEvent) event).getWhoClicked().getWorld();
//			} else if (event instanceof InventoryDragEvent) {
//				passTo = ((InventoryDragEvent) event).getWhoClicked().getWorld();
//			} else if (event instanceof InventoryInteractEvent) {
//				passTo = ((InventoryInteractEvent) event).getWhoClicked().getWorld();
//			} else if (event instanceof InventoryOpenEvent) {
//				passTo = ((InventoryOpenEvent) event).getPlayer().getWorld();
//			} else if (event instanceof PlayerEvent) {
//				passTo = ((PlayerEvent) event).getPlayer().getWorld();
//			} else if (event instanceof PlayerLeashEntityEvent) {
//				passTo = ((PlayerLeashEntityEvent) event).getPlayer().getWorld();
//			} else if (event instanceof VehicleEvent) {
//				passTo = ((VehicleEvent) event).getVehicle().getWorld();
//			} else if (event instanceof WeatherEvent) {
//				passTo = ((WeatherEvent) event).getWorld();
//			} else if (event instanceof WorldEvent) {
//				passTo = ((WorldEvent) event).getWorld();
//			}
//
//			if (passTo != null) {
//				callEvent(passTo, event);
//			} else {
//				for (World world : KitPlugin.getInstance().getServer().getWorlds()) {
//					callEvent(world, event);
//				}
//			}
//		}
//	}
//
//	private class MethodData {
//		Listener listener;
//		EventHandler handler;
//		Method method;
//	}
//
//	private class PerWorldListenerData {
//		private Listener listener;
//		private Map<Class<? extends Event>, List<Method>> methodMap;
//	}
//}

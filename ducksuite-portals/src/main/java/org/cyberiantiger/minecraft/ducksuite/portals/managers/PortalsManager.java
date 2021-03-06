package org.cyberiantiger.minecraft.ducksuite.portals.managers;

import org.cyberiantiger.minecraft.ducksuite.portals.DuckSuitePortals;
import org.cyberiantiger.minecraft.ducksuite.portals.objects.Portal;
import org.cyberiantiger.minecraft.ducksuite.portals.tasks.PluginMessageTask;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class PortalsManager {

    public static boolean RECEIVED = false;
    public static HashMap<World, ArrayList<Portal>> PORTALS = new HashMap<>();
    public static HashMap<String, Location> pendingTeleports = new HashMap<>();

    public static void deletePortal( String name, String string ) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream( b );
        try {
            out.writeUTF( "DeletePortal" );
            out.writeUTF( name );
            out.writeUTF( string );

        } catch ( IOException e ) {
            e.printStackTrace();
        }
        new PluginMessageTask( b ).runTaskAsynchronously( DuckSuitePortals.INSTANCE );

    }

    public static void removePortal( String name ) {
        Portal p = getPortal( name );
        System.out.println( "removing portal " + name );
        if ( p != null ) {
            PORTALS.get( p.getWorld() ).remove( p );
            p.clearPortal();
        }
    }

    public static Portal getPortal( String name ) {
        for ( ArrayList<Portal> list : PORTALS.values() ) {
            for ( Portal p : list ) {
                if ( p.getName().equals( name ) ) {
                    return p;
                }
            }
        }
        return null;
    }

    public static void getPortalsList( String name ) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream( b );
        try {
            out.writeUTF( "ListPortals" );
            out.writeUTF( name );

        } catch ( IOException e ) {
            e.printStackTrace();
        }
        new PluginMessageTask( b ).runTaskAsynchronously( DuckSuitePortals.INSTANCE );

    }

    public static void teleportPlayer( Player p, Portal portal ) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream( b );
        try {
            out.writeUTF( "TeleportPlayer" );
            out.writeUTF( p.getName() );
            out.writeUTF( portal.getType() );
            out.writeUTF( portal.getDestination() );
            out.writeBoolean( p.hasPermission( "ducksuite.portals.portal." + portal.getName() ) || p.hasPermission( "ducksuite.portals.portal.*" ) );

        } catch ( IOException e ) {
            e.printStackTrace();
        }
        new PluginMessageTask( b ).runTaskAsynchronously( DuckSuitePortals.INSTANCE );
    }

    public static void setPortal( CommandSender sender, String name, String type, String dest, String fill ) {

        Player p = ( Player ) sender;
        Selection sel = DuckSuitePortals.WORLDEDIT.getSelection( p );

        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream( b );
        try {
            out.writeUTF( "SetPortal" );
            out.writeUTF( sender.getName() );
            if ( sel == null || !( sel instanceof CuboidSelection ) ) {
                out.writeBoolean( false );
            } else {
                out.writeBoolean( true );
                out.writeUTF( name );
                out.writeUTF( type );
                out.writeUTF( dest );
                out.writeUTF( fill );
                Location max = sel.getMaximumPoint();
                Location min = sel.getMinimumPoint();
                out.writeUTF( max.getWorld().getName() );
                out.writeDouble( max.getX() );
                out.writeDouble( max.getY() );
                out.writeDouble( max.getZ() );
                out.writeUTF( min.getWorld().getName() );
                out.writeDouble( min.getX() );
                out.writeDouble( min.getY() );
                out.writeDouble( min.getZ() );
            }
        } catch ( IOException e ) {
            e.printStackTrace();
        }
        new PluginMessageTask( b ).runTaskAsynchronously( DuckSuitePortals.INSTANCE );

    }

    public static void addPortal( String name, String type, String dest, String filltype, Location max, Location min ) {
        if ( max.getWorld() == null ) {
            Bukkit.getConsoleSender().sendMessage( ChatColor.RED + "World does not exist portal " + name + " will not load :(" );
            return;
        }
        Portal portal = new Portal( name, type, dest, filltype, max, min );
        ArrayList<Portal> ps = PORTALS.get( max.getWorld() );
        if ( ps == null ) {
            ps = new ArrayList<>();
            PORTALS.put( max.getWorld(), ps );
        }
        ps.add( portal );
        portal.fillPortal();
    }

    public static void requestPortals() {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream( b );
        try {
            out.writeUTF( "RequestPortals" );
        } catch ( IOException e ) {
            e.printStackTrace();
        }
        new PluginMessageTask( b ).runTaskAsynchronously( DuckSuitePortals.INSTANCE );

    }

    public static void sendVersion() {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream( b );
        try {
            out.writeUTF( "SendVersion" );
            out.writeUTF( ChatColor.RED + "Portals - " + ChatColor.GOLD + DuckSuitePortals.INSTANCE.getDescription().getVersion() );
        } catch ( IOException e ) {
            e.printStackTrace();
        }
        new PluginMessageTask( b ).runTaskAsynchronously( DuckSuitePortals.INSTANCE );
    }
}

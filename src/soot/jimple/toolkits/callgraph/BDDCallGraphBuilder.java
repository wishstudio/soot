/* Soot - a J*va Optimization Framework
 * Copyright (C) 2002 Ondrej Lhotak
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package soot.jimple.toolkits.callgraph;
import soot.*;
import soot.options.*;
import soot.jimple.*;
import java.util.*;
import soot.util.*;
import soot.util.queue.*;

/** Models the call graph.
 * @author Ondrej Lhotak
 */
public final class BDDCallGraphBuilder
{ 
    private PointsToAnalysis pa;
    private CGOptions options;
    private BDDReachableMethods reachables;
    private boolean appOnly = false;
    private BDDOnFlyCallGraphBuilder ofcgb;
    private BDDCallGraph cg;

    public BDDCallGraph getCallGraph() { return cg; }

    public static BDDContextManager makeContextManager( BDDCallGraph cg ) {
        CGOptions options = new CGOptions( PhaseOptions.v().getPhaseOptions("cg") );
        if( options.context() == options.context_insens ) {
            return new BDDContextInsensitiveContextManager( cg );
        } else if( options.context() == options.context_1cfa ) {
            throw new RuntimeException("NYI");
            //return new OneCFAContextManager( cg );
        } else if( options.context() == options.context_objsens ) {
            throw new RuntimeException("NYI");
            //return new ObjSensContextManager( cg );
        } else throw new RuntimeException( "Unhandled context-sensitivity level." );
    }

    /** This constructor builds a complete call graph using the given
     * PointsToAnalysis to resolve virtual calls. */
    public BDDCallGraphBuilder( PointsToAnalysis pa ) {
        this.pa = pa;
        options = new CGOptions( PhaseOptions.v().getPhaseOptions("cg") );
        if( options.all_reachable() ) {
            List entryPoints = new ArrayList();
            entryPoints.addAll( EntryPoints.v().all() );
            entryPoints.addAll( EntryPoints.v().methodsOfApplicationClasses() );
            Scene.v().setEntryPoints( entryPoints );
        }
        cg = new BDDCallGraph();
        reachables = new BDDReachableMethods( cg, Scene.v().getEntryPoints() );
        BDDContextManager cm = makeContextManager(cg);
        ofcgb = new BDDOnFlyCallGraphBuilder( null, cm, reachables );
    }
    /*
    public void build() {
        QueueReader worklist = reachables.listener();
        while(true) {
            ofcgb.processReachables();
            reachables.update();
            MethodOrMethodContext momc = (MethodOrMethodContext) worklist.next();
            if( momc == null ) break;
            List receivers = (List) ofcgb.methodToReceivers().get(momc.method());
            if( receivers != null) for( Iterator receiverIt = receivers.iterator(); receiverIt.hasNext(); ) {     
                final Local receiver = (Local) receiverIt.next();
                final PointsToSet p2set = pa.reachingObjects( receiver );
                for( Iterator typeIt = p2set.possibleTypes().iterator(); typeIt.hasNext(); ) {
                    final Type type = (Type) typeIt.next();
                    ofcgb.addType( receiver, momc.context(), type, null );
                }
            }
            List stringConstants = (List) ofcgb.methodToStringConstants().get(momc.method());
            if( stringConstants != null ) for( Iterator stringConstantIt = stringConstants.iterator(); stringConstantIt.hasNext(); ) {     
                final Local stringConstant = (Local) stringConstantIt.next();
                PointsToSet p2set = pa.reachingObjects( stringConstant );
                Collection possibleStringConstants = p2set.possibleStringConstants();
                if( possibleStringConstants == null ) {
                    ofcgb.addStringConstant( stringConstant, momc.context(), null );
                } else {
                    for( Iterator constantIt = possibleStringConstants.iterator(); constantIt.hasNext(); ) {
                        final String constant = (String) constantIt.next();
                        ofcgb.addStringConstant( stringConstant, momc.context(), constant );
                    }
                }
            }
        }
    }
    */
}


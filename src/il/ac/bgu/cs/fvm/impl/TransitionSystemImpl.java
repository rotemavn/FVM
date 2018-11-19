package il.ac.bgu.cs.fvm.impl;

import il.ac.bgu.cs.fvm.exceptions.*;
import il.ac.bgu.cs.fvm.transitionsystem.Transition;
import il.ac.bgu.cs.fvm.transitionsystem.TransitionSystem;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class TransitionSystemImpl<S,A,P> implements TransitionSystem{
    private Set<Object> _states;
    private Set<Object> _initialStates;
    private Set<Object> _actions;
    private Set<Transition> _transitions;
    private Set<Object> _ap;
    private Map<Object,Set<Object>> _l_tagging;
    private String _name;

    public void init() {
        _states = new HashSet<>();
        _initialStates = new HashSet<>();
        _actions = new HashSet<>();
        _transitions = new HashSet<Transition>();
        _ap = new HashSet<>();
        _l_tagging = new HashMap<>();
        _name = "";
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public void setName(String name) {
        _name = name;
    }

    @Override
    public void addAction(Object anAction) {
        _actions.add(anAction);

    }

    @Override
    public void setInitial(Object aState, boolean isInitial) throws StateNotFoundException {
        if(_states.contains(aState)){
            if(isInitial){
                _initialStates.add(aState);
            }
            else{
                _initialStates.remove(aState);
            }
        }
        else
            throw new StateNotFoundException("Cannot set state as initial, since it does not exist.");

    }

    @Override
    public void addState(Object o) {
        _states.add(o);

    }

    @Override
    public void addTransition(Transition t) throws FVMException {
        Object from = t.getFrom();
        Object to = t.getTo();
        if(_states.contains(from) && _states.contains(to) && _actions.contains(t.getAction()))
            _transitions.add(t);
        else
            throw new InvalidTransitionException(t);
    }

    @Override
    public Set getActions() {
        return _actions;
    }

    @Override
    public void addAtomicProposition(Object p) {
        _ap.add(p);
    }

    @Override
    public Set getAtomicPropositions() {
        return _ap;
    }

    @Override
    public void addToLabel(Object s, Object l) throws FVMException {
        Set<Object> taggings = _l_tagging.get(s);
        if (taggings == null){
            taggings = new HashSet<>();
        }
        taggings.add(l);
        _l_tagging.put(s,taggings);
    }

    @Override
    public Set getLabel(Object s) {
        if(!_states.contains(s))
            throw new StateNotFoundException("Cannot find label of none existing state " +s.toString());
        Set<Object> label = _l_tagging.get(s);
        if(label == null){
            return new HashSet();
        }
        return label;
    }

    @Override
    public Set getInitialStates() {
        return _initialStates;
    }

    @Override
    public Map getLabelingFunction() {
        return _l_tagging;
    }

    @Override
    public Set getStates() {
        return _states;
    }

    @Override
    public Set<Transition> getTransitions() {
        return _transitions;
    }

    @Override
    public void removeAction(Object o) throws FVMException {
        for (Transition transition: _transitions) {
            if(transition.getAction().equals(o))
                throw new DeletionOfAttachedActionException(o, TransitionSystemPart.TRANSITIONS);
        }
        _actions.remove(o);
    }

    private Object searchTagging(Object p){
        for(Object state: _l_tagging.keySet()){
            Set<Object> taggingList = _l_tagging.get(state);
            if(taggingList.contains(p)){
                return state;
            }
        }
        return null;
    }

    @Override
    public void removeAtomicProposition(Object p) throws FVMException {
        Object state = searchTagging(p);
        if(state == null)
            _ap.remove(p);
        else
            throw new DeletionOfAttachedAtomicPropositionException(p, TransitionSystemPart.ATOMIC_PROPOSITIONS);

    }

    @Override
    public void removeLabel(Object s, Object l) {
        Set<Object> taggings = _l_tagging.get(s);
        taggings.remove(l);
        if(taggings.size() == 0)
            _l_tagging.remove(s);
        else
            _l_tagging.put(s,taggings);
    }

    @Override
    public void removeState(Object o) throws FVMException {
        if(_initialStates.contains(o))
            throw new DeletionOfAttachedStateException(o, TransitionSystemPart.INITIAL_STATES);

        for (Transition transition: _transitions) {
            if(transition.getFrom().equals(o) || transition.getTo().equals(o))
                throw new DeletionOfAttachedStateException(o, TransitionSystemPart.TRANSITIONS);
        }

        if(_l_tagging.containsKey(o)){
            throw new DeletionOfAttachedStateException(o, TransitionSystemPart.LABELING_FUNCTION);
        }
        _states.remove(o);
    }

    @Override
    public void removeTransition(Transition t) {
        _transitions.remove(t);

    }

}

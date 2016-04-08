import {Site} from '../model/site';
import {ISiteAction} from './sites.action';
export function reducerSites(state:Array<Site> = [], action:ISiteAction) {
    switch (action.type) {
        case 'LOAD_SITES':
            return action.sites;
        case 'ADD_SITE':
            state.push(action.site);
            return state;
        default:
            return state;
    }
}

import {Injectable} from 'angular2/core';
import {SitesService} from '../service/sites.service';
import {createStore,combineReducers} from 'redux';
import {reducerSites} from './sites.reducer';
import {loadSites,addSite} from './sites.action';
import {Site} from '../model/site';

@Injectable()
export class SitesStore {
    store:Redux.Store;

    constructor(private sitesService:SitesService) {
        this.store = createStore(combineReducers({
            sites: reducerSites
        }), {});
    }

    get sites() {
        return this.store.getState().sites;
    }

    addSite(name:string) {
        let obs = this.sitesService.addSite(name);
        obs.subscribe((site:Site)=> {
            this.store.dispatch(addSite(site));
        });
        return obs;
    }

    getSites() {
        this.sitesService.getSites().subscribe((sites:Array<Site>)=> {
            this.store.dispatch(loadSites(sites));
        });
    }
}

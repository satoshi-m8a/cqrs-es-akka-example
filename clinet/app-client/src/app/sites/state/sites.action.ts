import {Site} from '../model/site';

export interface ISiteAction {
    type:string;
    sites?:Array<Site>;
    site?:Site;
}

export function loadSites(sites:Array<Site>):ISiteAction {
    return {
        type: 'LOAD_SITES',
        sites: sites
    };
}

export function addSite(site:Site):ISiteAction {
    return {
        type: 'ADD_SITE',
        site: site
    };
}

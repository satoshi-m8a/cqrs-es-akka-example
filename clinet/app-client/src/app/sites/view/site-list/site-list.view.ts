import {Component} from 'angular2/core';
import {SiteListComponent} from '../../component/site-list/site-list.component';
import {SitesStore} from '../../state/sites.store';
import {AddSiteModalComponent} from '../../component/add-site-modal/add-site-modal.component';
import {ROUTER_DIRECTIVES} from 'angular2/router';

declare var jQuery:any;

@Component({
    templateUrl: 'app/sites/view/site-list/site-list.view.html',
    directives: [SiteListComponent, AddSiteModalComponent, ROUTER_DIRECTIVES]
})
export class SiteListView {
    constructor(private sitesStore:SitesStore) {
        sitesStore.getSites();
    }

    onClickAddSite() {
        jQuery('#addSiteModal').modal('show');
    }
}


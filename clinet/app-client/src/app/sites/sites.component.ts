import {Component} from 'angular2/core';
import {SitesService} from './service/sites.service';
import {SiteListView} from './view/site-list/site-list.view';
import {RouteConfig} from 'angular2/router';
import {SiteDetailView} from './view/site-detail/site-detail.view';
import {ROUTER_DIRECTIVES} from 'angular2/router';
import {SitesStore} from './state/sites.store';
@Component({
    template: '<router-outlet></router-outlet>',
    styleUrls: ['app/sites/sites.component.css'],
    providers: [SitesService, SitesStore],
    directives: [ROUTER_DIRECTIVES]
})
@RouteConfig([
    {path: '', name: 'SiteList', component: SiteListView, useAsDefault: true},
    {path: '/:id', name: 'SiteDetail', component: SiteDetailView}
])
export class SitesComponent {
}

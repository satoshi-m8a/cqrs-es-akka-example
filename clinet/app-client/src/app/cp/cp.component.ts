import {Component} from 'angular2/core';
import {SitesComponent} from '../sites/sites.component';
import {RouteConfig} from 'angular2/router';
import {ROUTER_DIRECTIVES} from 'angular2/router';
import {HeaderComponent} from './component/header/header.component';
import {DashboardComponent} from '../dashboard/view/dashboard.component';
import {ArticlesComponent} from '../articles/view/articles.component';
@Component({
    templateUrl: 'app/cp/cp.component.html',
    styleUrls: ['app/cp/cp.component.css'],
    directives: [ROUTER_DIRECTIVES, HeaderComponent]
})
@RouteConfig([
    {path: '', name: 'Dashboard', component: DashboardComponent, useAsDefault: true},
    {path: 'sites', name: 'Sites', component: SitesComponent},
    {path: 'articles', name: 'Articles', component: ArticlesComponent}
])
export class CpComponent {

}

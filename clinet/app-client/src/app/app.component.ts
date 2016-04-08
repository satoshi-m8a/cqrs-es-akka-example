import {Component} from 'angular2/core';
import {RouteConfig} from 'angular2/router';
import {ROUTER_DIRECTIVES} from 'angular2/router';
import {CpComponent} from './cp/cp.component';
@Component({
    selector: 'nv-app',
    template: '<router-outlet></router-outlet>',
    directives: [ROUTER_DIRECTIVES]
})
@RouteConfig([
    {path: ':sid/...', name: 'Cp', component: CpComponent}
])
export class AppComponent {
}

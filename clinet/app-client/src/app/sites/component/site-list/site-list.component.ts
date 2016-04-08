import {Component} from 'angular2/core';
import {Input} from 'angular2/core';
import {Site} from '../../model/site';
import {ROUTER_DIRECTIVES} from 'angular2/router';
import {Router} from 'angular2/router';
@Component({
    selector: 'nv-site-list',
    templateUrl: 'app/sites/component/site-list/site-list.component.html',
    directives: [ROUTER_DIRECTIVES]
})
export class SiteListComponent {
    @Input() list:Array<Site>;

    constructor(private router:Router) {
    }

    switch(site:Site) {
        this.router.navigate(['/Cp', {sid: site.id}, 'Dashboard']);
    }
}

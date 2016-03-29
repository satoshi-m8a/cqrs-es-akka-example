import {Component} from 'angular2/core';
import {OnInit} from 'angular2/core';
import {SitesService} from './service/sites.service';
@Component({
    templateUrl: 'app/sites/sites.component.html',
    styleUrls: ['app/sites/sites.component.css'],
    providers: [SitesService]
})
export class SitesComponent implements OnInit {

    constructor(sitesService:SitesService) {
        sitesService.getSites().subscribe(()=> {
            console.log('subs');
        });
    }

    ngOnInit():any {
        return null;
    }
}

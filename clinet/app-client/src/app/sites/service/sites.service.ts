import {Injectable} from 'angular2/core';
import {Http} from 'angular2/http';
import {Response} from 'angular2/http';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/share';
import {Observable} from 'rxjs/Observable';
import {Config} from '../../config';
import {Site} from '../model/site';
import {Headers} from 'angular2/http';


@Injectable()
export class SitesService {

    constructor(private http:Http, private config:Config) {
    }

    addSite(name:string):Observable<any> {
        let json = JSON.stringify({
            name: name
        });
        let headers = new Headers();
        headers.append('Content-Type', 'application/json');
        //noinspection TypeScriptUnresolvedFunction
        return this.http.post(this.config.API_URL + '/api/v1/sites', json, {
            headers: headers
        }).map((res:Response)=> {
            let item = res.json();
            return new Site(item.id, item.name);
        }).share();
    }

    getSites():Observable<any> {
        //noinspection TypeScriptUnresolvedFunction
        return this.http.get(this.config.API_URL + '/api/v1/sites').map((res:Response)=> {
            return res.json().map((item:any)=> {
                return new Site(item.id, item.name);
            });
        }).share();
    }
}

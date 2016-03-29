import {Injectable} from 'angular2/core';
import {Http} from 'angular2/http';
import {Response} from 'angular2/http';
import 'rxjs/add/operator/map';
import {Observable} from 'rxjs/Observable';

@Injectable()
export class SitesService {

    constructor(private http:Http) {
    }

    getSites():Observable<any> {
        //noinspection TypeScriptUnresolvedFunction
        return this.http.get('http://jsonplaceholder.typicode.com/posts').map((res:Response)=> {
            console.log(res);
            return '';
        });
    }
}

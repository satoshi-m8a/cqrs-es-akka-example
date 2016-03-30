import {Injectable} from 'angular2/core';
import {Http} from 'angular2/http';
import {Observable} from 'rxjs/Observable';
import {Discussion} from '../model/discussion';
import {Response} from 'angular2/http';
import {Comment} from '../model/comment';
import {Config} from '../../config';

@Injectable()
export class DiscussionsService {
    constructor(private http:Http, private config:Config) {
    }

    getDiscussions():Observable<Array<Discussion>> {
        //noinspection TypeScriptUnresolvedFunction
        return this.http.get(this.config.API_URL + '/api/v1/discussions').map((res:Response)=> {
            return [new Discussion('id', 'title')];
        });
    }

    getDiscussion(id:string):Observable<Discussion> {
        //noinspection TypeScriptUnresolvedFunction
        return this.http.get(this.config.API_URL + `/api/v1/discussions/${id}`).map((res:Response)=> {
            console.log(res);
            return new Discussion('id', 'title');
        });
    }

    getComments(id:string):Observable<Array<Comment>> {
        //noinspection TypeScriptUnresolvedFunction
        return this.http.get(this.config.API_URL + `/api/v1/discussions/${id}/comments`).map((res:Response)=> {
            console.log(res.json());
            return res.json().map((d:any)=> {
                return new Comment(d.id, d.text);
            });
        });
    }
}

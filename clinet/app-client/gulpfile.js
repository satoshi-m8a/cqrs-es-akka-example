var gulp = require('gulp'),
    template = require('gulp-template'),
    inject = require('gulp-inject'),
    rename = require('gulp-rename'),
    ts = require('gulp-typescript'),
    sourcemaps = require('gulp-sourcemaps'),
    sass = require('gulp-sass'),
    concat = require('gulp-concat'),
    uglify = require('gulp-uglify'),
    inlineNg2Template = require('gulp-inline-ng2-template'),
    tslint = require('gulp-tslint'),
    rimraf = require('rimraf'),
    browserSync = require('browser-sync'),
    fs = require('fs'),
    historyApiFallback = require('connect-history-api-fallback'),
    Builder = require('systemjs-builder');


const SRC = 'src';
const DIST = 'dist';
const BUILD = 'build';

const LIBS = [
    './node_modules/jquery/dist/jquery.js',
    './node_modules/tether/dist/js/tether.js',
    './node_modules/bootstrap/dist/js/bootstrap.js',
    './node_modules/es6-shim/es6-shim.min.js',
    './node_modules/systemjs/dist/system-polyfills.js',
    './node_modules/angular2/es6/dev/src/testing/shims_for_IE.js',
    './node_modules/angular2/bundles/angular2-polyfills.js',
    './node_modules/systemjs/dist/system.src.js',
    './node_modules/rxjs/bundles/Rx.js',
    './node_modules/angular2/bundles/angular2.dev.js',
    './node_modules/angular2/bundles/router.js',
    './node_modules/angular2/bundles/http.js'
];

var config = {
    TITLE: '',
    ENV: 'dev'
};

var files = function (list) {
    return list.map(function (l) {
        return l.replace(/.*\//, '');
    });
};

gulp.task('configure:dev', function () {
    config = {
        TITLE: '(DEV)NV CMS Development Mode(DEV)',
        ENV: 'dev'
    }
});

gulp.task('configure:prod', function () {
    config = {
        TITLE: 'NV CMS',
        ENV: 'prod'
    }
});

gulp.task('template', ['config-template', 'main-template']);

gulp.task('config-template', function () {
    return gulp.src(SRC + '/config.tpl.ts')
        .pipe(template(config))
        .pipe(rename('config.ts'))
        .pipe(gulp.dest(SRC));
});

gulp.task('main-template', function () {
    return gulp.src(SRC + '/main.tpl.ts')
        .pipe(template(config))
        .pipe(rename('main.ts'))
        .pipe(gulp.dest(SRC));
});

gulp.task('ts', function () {
    var tsProject = ts.createProject('tsconfig.json');

    var tsResult = gulp.src([
            'typings/browser.d.ts',
            'src/**/*.ts',
            '!src/**/*.tpl.ts',
            '!src/**/*.spec.ts',
            '!src/**/*.e2e.ts'
        ])
        .pipe(sourcemaps.init())
        .pipe(ts(tsProject));

    return tsResult.js
        .pipe(sourcemaps.write())
        .pipe(browserSync.reload({stream: true}))
        .pipe(gulp.dest(DIST))
});


gulp.task('ts:inline', function () {
    var tsProject = ts.createProject('tsconfig.json');

    var tsResult = gulp.src([
            'typings/browser.d.ts',
            'src/**/*.ts',
            '!src/**/*.tpl.ts',
            '!src/**/*.spec.ts',
            '!src/**/*.e2e.ts'
        ])
        .pipe(sourcemaps.init())
        .pipe(inlineNg2Template({base: SRC}))
        .pipe(ts(tsProject));

    return tsResult.js
        .pipe(sourcemaps.write())
        .pipe(gulp.dest(DIST))
});

gulp.task('ts:lint', function () {
    var lintConf = JSON.parse(fs.readFileSync('tslint.json').toString());

    return gulp.src([
            '!typings/browser.d.ts',
            'src/**/*.ts',
            '!src/**/*.tpl.ts',
            'src/**/*.spec.ts',
            'src/**/*.e2e.ts'
        ])
        .pipe(tslint({
            rulesDirectory: lintConf.rulesDirectory
        }))
        .pipe(tslint.report(require('tslint-stylish'), {
            emitError: require('is-ci'),
            sort: true,
            bell: true
        }));
});

gulp.task('copy-libs', function () {
    return gulp.src(LIBS)
        .pipe(rename({dirname: 'libs'}))
        .pipe(gulp.dest(DIST));
});

gulp.task('index-html', ['sass', 'ts', 'copy-libs'], function () {
    var styles = ['styles/main.css'];
    var libs = files(LIBS).map(function (l) {
        return 'libs/' + l;
    });

    var sources = gulp.src(libs.concat(styles), {read: false, 'cwd': '' + __dirname + '/' + DIST});

    return gulp.src(SRC + '/index.html')
        .pipe(template(config))
        .pipe(inject(sources, {relative: false, removeTags: false}))
        .pipe(gulp.dest(DIST));
});

gulp.task('index-html:vendor', ['sass', 'vendor'], function () {
    var styles = ['styles/main.css'];
    var libs = ['vendor.js'];

    var sources = gulp.src(libs.concat(styles), {read: false, 'cwd': '' + __dirname + '/' + DIST});

    return gulp.src(SRC + '/index.html')
        .pipe(template(config))
        .pipe(inject(sources, {relative: false, removeTags: true}))
        .pipe(gulp.dest(DIST));
});

gulp.task('vendor', function () {
    return gulp.src(LIBS)
        .pipe(sourcemaps.init())
        .pipe(concat('vendor.js'))
        .pipe(uglify({mangle: false}))
        .pipe(sourcemaps.write())
        .pipe(gulp.dest(DIST));
});

gulp.task('bundle', ['ts:inline'], function () {
    var builder = new Builder({
        defaultJSExtensions: true,
        paths: {
            'dist/*': 'dist/*',
            '*': 'node_modules/*'
        }
    });

    return builder.buildStatic(DIST + '/main.js', DIST + '/bundle.js', {format: 'cjs', minify: true, mangle: false});
});

gulp.task('copy', ['bundle', 'index-html:vendor'], function () {
    return gulp.src([
            DIST + '/vendor.js',
            DIST + '/bundle.js',
            DIST + '/index.html',
            DIST + '/styles/**/*'
        ], {base: DIST})
        .pipe(gulp.dest('./' + BUILD));
});

gulp.task('css', function () {
    return gulp.src(SRC + '/**/*.css', {base: SRC})
        .pipe(browserSync.reload({stream: true}))
        .pipe(gulp.dest(DIST));
});

gulp.task('sass', function () {
    return gulp.src(SRC + '/**/*.scss')
        .pipe(sass())
        .pipe(browserSync.stream({match: '**/*.css'}))
        .pipe(gulp.dest(DIST));
});

gulp.task('html', function () {
    return gulp.src(SRC + '/app/**/*.html', {base: SRC})
        .pipe(browserSync.stream({match: '**/*.html'}))
        .pipe(gulp.dest(DIST));
});

gulp.task('serve', function () {
    browserSync.init({
        server: {
            baseDir: './' + DIST,
            middleware: [ historyApiFallback() ]
        },
        open: false
    });

    gulp.watch(DIST + '/**/*.css').on('change', browserSync.reload);

    gulp.watch(SRC + '/index.html', ['index-html']);
    gulp.watch(SRC + '/**/*.ts', ['ts:lint','ts']);
    gulp.watch(SRC + '/**/*.scss', ['sass']);
    gulp.watch(SRC + '/**/*.css', ['css']);
    gulp.watch(SRC + '/app/**/*.html', ['html']);
});


gulp.task('clean-dist', function (cb) {
    return rimraf(DIST, cb);
});

gulp.task('clean-build', function (cb) {
    return rimraf(BUILD, cb);
});

gulp.task('default', ['dev']);

gulp.task('clean', ['clean-dist', 'clean-build']);

gulp.task('dev:build', ['configure:dev', 'template', 'ts:lint', 'ts', 'sass', 'css', 'html', 'copy-libs', 'index-html']);

gulp.task('dev:serve', ['configure:dev', 'template', 'ts:lint', 'ts', 'sass', 'css', 'html', 'copy-libs', 'index-html', 'serve']);

gulp.task('prod:build', ['configure:prod', 'template', 'ts:lint', 'ts:inline', 'sass', 'bundle', 'vendor', 'index-html:vendor', 'copy']);